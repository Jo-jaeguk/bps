package com.mobilityk.core.service

import com.mobilityk.core.domain.ActivityLog
import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuidePrice
import com.mobilityk.core.domain.LogType
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.Member
import com.mobilityk.core.dto.ActivityLogDTO
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.api.guide.GuidePriceVM
import com.mobilityk.core.dto.mapper.ActivityLogMapper
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.repository.ActivityLogRepository
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.search.ActivityLogSearchOption
import com.mobilityk.core.util.CommonUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class ActivityLogService(
    private val activityLogRepository: ActivityLogRepository,
    private val memberRepository: MemberRepository,
    private val activityLogMapper: ActivityLogMapper,
    private val memberMapper: MemberMapper
) {

    @Transactional
    fun findAllBy(pageable: Pageable): Page<ActivityLogDTO> {
        return activityLogRepository.findAll(pageable)
            .map {
                activityLogMapper.toDto(it)
            }
    }

    @Transactional
    fun findAllBySearchOption(searchOption: ActivityLogSearchOption, pageable: Pageable): Page<ActivityLogDTO> {
        return activityLogRepository.findAllBySearch(searchOption, pageable)
    }

    @Transactional
    fun createMemberInfoLog(memberId: Long, adminId: Long, changeLog: String): Unit {
        val memberOpt = memberRepository.findById(memberId)
        val member = if(memberOpt.isEmpty) { MemberDTO() } else { memberMapper.toDto(memberOpt.get())}

        val activity = ActivityLog(
            memberId = member.id,
            writerId = adminId,
            logType = LogType.MEMBER_LOG,
            body = changeLog,
            memberBranch = member.branch,
            memberEmail = member.emailAddress,
            memberGroup = member.groupName,
            memberName = member.name,
            memberPhone = member.phone,
            memberPosition = member.position
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createMemberPointLog(memberId: Long, adminId: Long, beforePoint: Long, afterPoint: Long): Unit {
        val memberOpt = memberRepository.findById(memberId)
        val member = if(memberOpt.isEmpty) { MemberDTO() } else { memberMapper.toDto(memberOpt.get())}

        val activity = ActivityLog(
            memberId = member.id,
            writerId = adminId,
            logType = LogType.MEMBER_LOG,
            body = member.emailAddress + "회원 포인트 변경 (" + CommonUtil.comma(beforePoint.toInt()) + ") -> (" + CommonUtil.comma(afterPoint.toInt()) + ")",
            memberBranch = member.branch,
            memberEmail = member.emailAddress,
            memberGroup = member.groupName,
            memberName = member.name,
            memberPhone = member.phone,
            memberPosition = member.position
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createAdminMadeLog(adminId: Long, adminDTO: MemberDTO): Unit {
        val activity = ActivityLog(
            memberId = adminDTO.id,
            writerId = adminId,
            logType = LogType.MEMBER_LOG,
            body = adminDTO.emailAddress + " 관리자 계정 생성",
            memberBranch = adminDTO.branch,
            memberEmail = adminDTO.emailAddress,
            memberGroup = adminDTO.groupName,
            memberName = adminDTO.name,
            memberPhone = adminDTO.phone,
            memberPosition = adminDTO.position
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createMemberPasswordLog(memberId: Long, adminId: Long, password: String ): Unit {
        val memberOpt = memberRepository.findById(memberId)
        val member = if(memberOpt.isEmpty) { MemberDTO() } else { memberMapper.toDto(memberOpt.get())}

        val activity = ActivityLog(
            memberId = member.id,
            writerId = adminId,
            logType = LogType.MEMBER_LOG,
            body = member.emailAddress + "회원 비밀번호 변경",
            memberBranch = member.branch,
            memberEmail = member.emailAddress,
            memberGroup = member.groupName,
            memberName = member.name,
            memberPhone = member.phone,
            memberPosition = member.position
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createMemberRoleChangeLog(memberId: Long, adminId: Long, beforeRoles: Set<ROLE>, afterRoles: Set<ROLE>): Unit {
        val memberOpt = memberRepository.findById(memberId)
        val member = if(memberOpt.isEmpty) { MemberDTO() } else { memberMapper.toDto(memberOpt.get())}

        var beforeRoleStr = ""
        beforeRoles.forEach { role ->
            beforeRoleStr += role.description + ","
        }
        if(beforeRoleStr.endsWith(",")) {
            beforeRoleStr = beforeRoleStr.substring(0, beforeRoleStr.length - 1)
        }

        var afterRoleStr = ""
        afterRoles.forEach { role ->
            afterRoleStr += role.description + ","
        }
        if(afterRoleStr.endsWith(",")) {
            afterRoleStr = afterRoleStr.substring(0, afterRoleStr.length - 1)
        }

        val activity = ActivityLog(
            memberId = member.id,
            writerId = adminId,
            logType = LogType.MEMBER_LOG,
            body = "권한 변경 ($beforeRoleStr) -> ($afterRoleStr)",
            memberBranch = member.branch,
            memberEmail = member.emailAddress,
            memberGroup = member.groupName,
            memberName = member.name,
            memberPhone = member.phone,
            memberPosition = member.position
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createGuideLogV2(member: Member, adminId: Long, logMessage: String) {
        val activity = ActivityLog(
            memberId = member.id,
            writerId = adminId,
            logType = LogType.GUIDE_LOG,
            body = logMessage,
            memberBranch = member.branch,
            memberEmail = member.emailAddress,
            memberGroup = member.groupName,
            memberName = member.name,
            memberPhone = member.phone,
            memberPosition = member.position
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createGuideLog(guide: Guide, adminId: Long, beforeList: List<GuidePrice>, afterList: List<GuidePriceVM> ): Unit {
        val memberOpt = memberRepository.findById(guide.memberId!!)
        val member = if(memberOpt.isEmpty) { MemberDTO() } else { memberMapper.toDto(memberOpt.get())}
        val builder = StringBuilder()

        builder.append(member.name + "고객 가이드 수정").append("\n")
        builder.append("가이드 번호 [" + guide.serial + "]").append("\n")
        afterList.forEach { afterPrice ->
            var changed = false
            val marketType = if(afterPrice.marketType == MarketType.RETAIL) "소매가" else "경매가"
            beforeList.forEach { beforePrice ->
                if(beforePrice.marketName == afterPrice.marketName &&
                    beforePrice.marketType == afterPrice.marketType) {
                    if(beforePrice.price != afterPrice.price) {
                        changed = true
                        builder.append(marketType).append(" ").append(afterPrice.marketName).append(" ")
                            .append("(").append(CommonUtil.comma(beforePrice.price?.toInt()!!)).append(")").append(" 만원")
                            .append(" -> ")
                            .append("(").append(CommonUtil.comma(afterPrice.price?.toInt()!!)).append(")").append(" 만원")
                            .append(" 변경")
                            .append("\n")
                    }
                }
            }
            if(!changed) {
                builder.append(marketType).append(" ").append(afterPrice.marketName).append(" ")
                    .append("(").append(CommonUtil.comma(0)).append(")").append(" 만원")
                    .append(" -> ")
                    .append("(").append(CommonUtil.comma(afterPrice.price?.toInt()!!)).append(")").append(" 만원")
                    .append(" 변경")
                    .append("\n")
            }
        }

        if(!builder.toString().isNullOrEmpty()) {
            val activity = ActivityLog(
                memberId = member.id,
                writerId = adminId,
                logType = LogType.GUIDE_LOG,
                body = builder.toString(),
                memberBranch = member.branch,
                memberEmail = member.emailAddress,
                memberGroup = member.groupName,
                memberName = member.name,
                memberPhone = member.phone,
                memberPosition = member.position
            )
            activityLogMapper.toDto(activityLogRepository.save(activity))
        }
    }

    @Transactional
    fun createGuideConfigRetailLog(adminId: Long, beforeValue: Double, afterValue: Double ): Unit {
        val activity = ActivityLog(
            memberId = null,
            writerId = adminId,
            logType = LogType.GUIDE_CONFIG_LOG,
            body = "소매가 결정 상수 변경 ($beforeValue) -> ($afterValue) 변경"
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createGuideConfigEverageLog(adminId: Long, chageLog: String ): Unit {
        val activity = ActivityLog(
            memberId = null,
            writerId = adminId,
            logType = LogType.GUIDE_CONFIG_LOG,
            body = chageLog
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }

    @Transactional
    fun createGuideConfigTableLog(adminId: Long, chageLog: String): Unit{
        val activity = ActivityLog(
            memberId = null,
            writerId = adminId,
            logType = LogType.GUIDE_CONFIG_LOG,
            body = chageLog
        )
        activityLogMapper.toDto(activityLogRepository.save(activity))
    }
}
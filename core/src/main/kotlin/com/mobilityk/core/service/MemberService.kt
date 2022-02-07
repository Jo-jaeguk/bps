package com.mobilityk.core.service

import com.mobilityk.core.domain.AccountType
import com.mobilityk.core.domain.AppleAccount
import com.mobilityk.core.domain.EmailAccount
import com.mobilityk.core.domain.Gender
import com.mobilityk.core.domain.KakaoAccount
import com.mobilityk.core.domain.Member
import com.mobilityk.core.domain.NaverAccount
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.api.kakao.KakaoUserInfo
import com.mobilityk.core.dto.api.member.MemberJoinVM
import com.mobilityk.core.dto.api.member.MemberVM
import com.mobilityk.core.dto.api.member.PasswordVM
import com.mobilityk.core.dto.api.naver.NaverUserInfo
import com.mobilityk.core.dto.api.nice.NiceUserInfo
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.exception.AlreadyExistUserException
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.AppleAccountRepository
import com.mobilityk.core.repository.EmailAccountRepository
import com.mobilityk.core.repository.KakaoAccountRepository
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.NaverAccountRepository
import com.mobilityk.core.repository.search.MemberSearchOption
import com.mobilityk.core.util.CommonUtil
import javassist.NotFoundException
import mu.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
data class MemberService(
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper,
    private val activityLogService: ActivityLogService,
    private val passwordEncoder: PasswordEncoder,
    private val kakaoAccountRepository: KakaoAccountRepository,
    private val naverAccountRepository: NaverAccountRepository,
    private val kakaoTalkService: KakaoTalkService,
    private val messageSource: MessageSource,
    private val emailAccountRepository: EmailAccountRepository,
    private val appleAccountRepository: AppleAccountRepository,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun findByRefreshToken(refreshToken: String): Member{
        val member = memberRepository.findByRefreshToken(refreshToken)
        return if(member.isPresent) {
            member.get()
        } else {
            throw NotFoundException("유저 정보를 찾을 수 없습니다.")
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateRefreshToken(memberId: Long, refreshToken: String) {
        val member = memberRepository.findById(memberId).orElseThrow { CommException("not found member") }
        member.refreshToken = refreshToken
    }

    @Transactional
    fun count(): Long {
        return memberRepository.count()
    }

    @Transactional
    fun findById(memberId: Long): MemberDTO {
        return memberMapper.toDto(memberRepository.findById(memberId).get())
    }

    @Transactional
    fun findAllBySearch(searchOption: MemberSearchOption, pageable: Pageable): Page<MemberDTO> {
        return memberRepository.findAllBySearch(searchOption, pageable)
            .map { member ->
                memberMapper.toDto(member)
            }
    }

    @Transactional
    fun findByPhone(phone: String): MemberDTO {
        val member = memberRepository.findByPhone(phone).orElseThrow { throw CommException("not found user") }
        return memberMapper.toDto(member)
    }



    @Transactional
    fun loginByApple(uid: String): MemberDTO {
        val appleAccount = appleAccountRepository.findByAppleUid(uid).orElseThrow { CommException("not found account") }
        if(appleAccount.memberId == null) {
            throw CommException("not found member")
        }
        val member = memberRepository.findById(appleAccount.memberId!!).orElseThrow { CommException("not found member") }
        if(!member.accessYn!!) throw CommException("no permission")
        return memberMapper.toDto(member)
    }

    @Transactional
    fun findByEmailAddress(emailAddress: String): Member {
        val member = memberRepository.findByEmailAddress(emailAddress)
        return if(member.isPresent) {
            member.get()
        } else {
            throw NotFoundException("유저 정보를 찾을 수 없습니다.")
        }
    }

    @Transactional
    fun getAdmins(): List<MemberDTO> {
        return memberMapper.toDtoList(memberRepository.findAllByRolesIn(listOf(ROLE.MASTER, ROLE.ADMIN)))
    }

    @Transactional
    fun updatePoint(memberId: Long, adminId: Long, memberVM: MemberVM): MemberDTO {
        val member = memberRepository.findById(memberId)
        if(member.isEmpty) throw CommException("not found member")

        val stored = member.get()
        val beforePoint = stored.point ?: 0L

        logger.info { "memberVM.point!! ${memberVM.point!!}" }
        stored.increasePoint(memberVM.point!!)
        logger.info { "stored.point!! ${stored.point!!}" }

        activityLogService.createMemberPointLog(memberId, adminId, beforePoint, stored.point!!)

        return memberMapper.toDto(stored)
    }

    @Transactional
    fun createAdmin(adminId: Long, memberVM: MemberVM): MemberDTO {
        var member = memberRepository.findByEmailAddress(memberVM.emailAddress!!)
        if(member.isPresent) throw CommException("아이디가 이미 존재합니다.")

        member = memberRepository.findByPhone(memberVM.phone!!)
        if(member.isPresent) throw CommException("휴대폰번호가 이미 존재합니다.")

        val admin = memberRepository.save(
            Member(
                emailAddress = memberVM.emailAddress,
                password = passwordEncoder.encode(memberVM.password),
                name = memberVM.name,
                accountType = AccountType.EMAIL,
                birthDate = LocalDateTime.now(),
                roles = mutableSetOf(ROLE.ADMIN),
                activated = true,
                accessYn = true,
                position = memberVM.position,
                point = 0L,
                groupName = memberVM.groupName,
                branch = memberVM.branch,
                accessToken = "",
                kakaoTalkYn = true,
                refreshToken = "",
                guideCount = 0,
                gender = memberVM.gender,
                phone = memberVM.phone
            )
        )
        val adminDTO =  memberMapper.toDto(admin)
        activityLogService.createAdminMadeLog(adminId, adminDTO)
        return adminDTO
    }


    @Transactional
    fun updatePassword(memberId: Long, adminId: Long, memberVM: MemberVM): MemberDTO {
        val member = memberRepository.findById(memberId)
        if(member.isEmpty) throw CommException("not found member")

        val stored = member.get()
        stored.password = passwordEncoder.encode(memberVM.password)

        activityLogService.createMemberPasswordLog(memberId, adminId, memberVM.password!!)

        return memberMapper.toDto(stored)
    }

    @Transactional
    fun deActivate(memberId: Long, adminId: Long): MemberDTO {
        val member = memberRepository.findById(memberId)
        if(member.isEmpty) throw CommException("not found member")

        val stored = member.get()
        stored.activated = false

        val builder = StringBuilder()
        builder.append(stored.name).append(" 회원 탈퇴 처리")
        activityLogService.createMemberInfoLog(memberId, adminId, builder.toString())

        return memberMapper.toDto(stored)
    }

    @Transactional
    fun joinByApple(uid: String, name: String?, email: String?): Long {
        val appleOpt = appleAccountRepository.findByAppleUid(uid)
        if(appleOpt.isPresent) {
            return appleOpt.get().id!!
        }
        val memberOpt = memberRepository.findByEmailAddress("apple.$uid")
        if(memberOpt.isPresent){
            throw AlreadyExistUserException("already exist member")
        }
        return appleAccountRepository.save(
            AppleAccount(
                appleUid = uid,
                name = name,
                email = email
            )
        ).id!!
    }

    @Transactional
    fun joinByKakao(kakaoUserInfo: KakaoUserInfo): Long {
        val kakaoAccountOpt = kakaoAccountRepository.findByKakaoId(kakaoUserInfo.id!!)
        if(kakaoAccountOpt.isPresent) {
            return kakaoAccountOpt.get().id!!
        }
        val memberOpt = memberRepository.findByEmailAddress("kakao." + kakaoUserInfo.id)
        if(memberOpt.isPresent){
            throw AlreadyExistUserException("already exist member")
        }
        /*
        val savedMember = createMember(
            MemberJoinVM(
                emailAddress = "kakao." + kakaoUserInfo.id,
                password = passwordEncoder.encode("1234"),
                accountType = AccountType.KAKAO
            )
        )
         */
        return kakaoAccountRepository.save(
            KakaoAccount(
                kakaoId = kakaoUserInfo.id,
                ageRange = kakaoUserInfo.kakao_account?.age_range,
                birthDay = kakaoUserInfo.kakao_account?.birthday,
                birthYear = kakaoUserInfo.kakao_account?.birthyear,
                email = kakaoUserInfo.kakao_account?.email,
                gender = kakaoUserInfo.kakao_account?.gender,
                nickName = kakaoUserInfo.kakao_account?.profile?.nickname,
                phoneNumber = kakaoUserInfo.kakao_account?.phone_number,
                profileImageUrl = kakaoUserInfo.kakao_account?.profile?.profile_image_url,
                thumbnailImageUrl = kakaoUserInfo.kakao_account?.profile?.thumbnail_image_url
            )
        ).id!!
    }

    @Transactional
    fun loginByKakao(kakaoUserInfo: KakaoUserInfo): MemberDTO {
        val kakaoAccount = kakaoAccountRepository.findByKakaoId(kakaoUserInfo.id!!).orElseThrow { CommException("not found kakao account") }
        if(kakaoAccount.memberId == null) {
            throw CommException("not found member")
        }
        val member = memberRepository.findById(kakaoAccount.memberId!!).orElseThrow { CommException("not found member") }
        if(!member.accessYn!!) throw CommException("no permission")
        return memberMapper.toDto(member)
    }


    @Transactional
    fun joinByNaver(naverUserInfo: NaverUserInfo): Long {

        val naverAccountOpt = naverAccountRepository.findByNaverId(naverUserInfo.response?.id!!)
        if(naverAccountOpt.isPresent) {
            return naverAccountOpt.get().id!!
        }
        val memberOpt = memberRepository.findByEmailAddress("naver." + naverUserInfo.response?.id)
        if(memberOpt.isPresent){
            throw AlreadyExistUserException("already exist member")
        }
        /*
        val savedMember = createMember(
            MemberJoinVM(
                emailAddress = "naver." + naverUserInfo.response?.id,
                password = passwordEncoder.encode("1234"),
                accountType = AccountType.NAVER
            )
        )
         */
        return naverAccountRepository.save(
            NaverAccount(
                naverId = naverUserInfo.response?.id,
                nickname = naverUserInfo.response?.nickname,
                name = naverUserInfo.response?.name,
                email = naverUserInfo.response?.email,
                gender = naverUserInfo.response?.gender,
                age = naverUserInfo.response?.age,
                birthday = naverUserInfo.response?.birthday,
                profileImage = naverUserInfo.response?.profileImage,
                birthyear = naverUserInfo.response?.birthyear,
                mobile = naverUserInfo.response?.mobile,
            )
        ).id!!
    }

    @Transactional
    fun loginByNaver(naverUserInfo: NaverUserInfo): MemberDTO {
        val naverAccount = naverAccountRepository.findByNaverId(naverUserInfo.response?.id!!).orElseThrow { CommException("not found naver account") }
        if(naverAccount.memberId == null) throw CommException("not found member")
        val member = memberRepository.findById(naverAccount.memberId!!).orElseThrow { CommException("not found member") }
        if(!member.accessYn!!) throw CommException("no permission")
        return memberMapper.toDto(member)
    }


    @Transactional
    fun createEmailAccount(email: String, password: String): Long {
        val memberOpt = memberRepository.findByEmailAddress(email)
        if(memberOpt.isPresent) {
            throw CommException("이미 존재하는 이메일 주소 입니다.")
        }
        return emailAccountRepository.save(
            EmailAccount(
                email = email,
                password = passwordEncoder.encode(password)
            )
        ).id!!
    }

    @Transactional
    fun createMember(memberJoinVM: MemberJoinVM): MemberDTO {
        val memberOpt = memberRepository.findByEmailAddress(memberJoinVM.emailAddress!!)
        if(memberOpt.isPresent) {
            throw CommException("이미 존재하는 이메일 주소 입니다.")
        }
        memberJoinVM.password = passwordEncoder.encode(memberJoinVM.password)
        val member = Member()
        member.createMember(memberJoinVM)
        return memberMapper.toDto(memberRepository.save(member))
    }

    @Transactional
    fun changePhone(memberId: Long, phone: String) {
        val member = memberRepository.findById(memberId).orElseThrow { CommException("not found user") }
        val memberOpt = memberRepository.findByPhone(phone)
        if(memberOpt.isPresent) {
            throw CommException("already phone")
        }
        member.phone = phone
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun changePersonalInfo(memberId: Long, niceUserInfo: NiceUserInfo) {
        val member = memberRepository.findById(memberId).orElseThrow { CommException("not found user") }
        val memberOpt = memberRepository.findByPhone(niceUserInfo.phone!!)
        if(memberOpt.isPresent) {
            throw CommException("already phone")
        }
        member.phone = niceUserInfo.phone!!
        member.name = niceUserInfo.name
        member.birthDate = LocalDateTime.of(
            LocalDate.parse(niceUserInfo.birthDate, DateTimeFormatter.ofPattern("yyyyMMdd")),
            LocalTime.of(0,0,0)
        )
        member.gender = if(niceUserInfo.gender == "1") Gender.MALE else Gender.FEMALE
    }

    @Transactional
    fun joinSuccess(
        niceUserInfo: NiceUserInfo,
        accountType: AccountType,
        accountId: Long
    ): MemberDTO {

        val memberOpt = memberRepository.findByPhone(niceUserInfo.phone!!)
        if(memberOpt.isPresent) {
            throw CommException("already phone")
        }

        val member = memberRepository.save(
            Member(
                name = niceUserInfo.name,
                phone = niceUserInfo.phone,
                birthDate = LocalDateTime.of(
                    LocalDate.parse(niceUserInfo.birthDate, DateTimeFormatter.ofPattern("yyyyMMdd")),
                    LocalTime.of(0,0,0)
                ),
                gender = if(niceUserInfo.gender == "1") Gender.MALE else Gender.FEMALE,
                roles = mutableSetOf(ROLE.NEW),
                password = "",
                guideCount = 0L,
                emailAddress = "",
                activated = true,
                refreshToken = "",
                accessToken = "",
                accessYn = false,
                accountType = accountType,
                branch = "",
                comment = "",
                groupName = "",
                point = 0L,
                position = "",
                kakaoTalkYn = true,
            )
        )
        when(accountType) {
            AccountType.NAVER -> {
                val account = naverAccountRepository.findById(accountId).orElseThrow { CommException("not found account") }
                member.emailAddress = "naver.${account.naverId}"
                member.password = passwordEncoder.encode("1234")
                account.memberId = member.id
            }
            AccountType.KAKAO -> {
                val account = kakaoAccountRepository.findById(accountId).orElseThrow { CommException("not found account") }
                member.emailAddress = "kakao.${account.kakaoId}"
                member.password = passwordEncoder.encode("1234")
                account.memberId = member.id
            }
            AccountType.EMAIL -> {
                val account = emailAccountRepository.findById(accountId).orElseThrow { CommException("not found account") }
                member.emailAddress = account.email
                member.password = account.password
                account.memberId = member.id
            }
            AccountType.APPLE -> {
                val account = appleAccountRepository.findById(accountId).orElseThrow { CommException("not found account") }
                member.emailAddress = "apple.${account.appleUid}"
                member.password = passwordEncoder.encode("1234")
                account.memberId = member.id
            }
        }
        return memberMapper.toDto(member)
    }

    @Transactional(rollbackFor = [RuntimeException::class, CommException::class])
    fun updatePassword(memberId: Long, passwordVM: PasswordVM): MemberDTO {
        val optional = memberRepository.findById(memberId)
        if(optional.isEmpty) throw CommException("no member")

        val stored = optional.get()
        if(stored.id != memberId) throw CommException("invalid member")

        stored.password = passwordEncoder.encode(passwordVM.password)
        return memberMapper.toDto(stored)
    }


    @Transactional(rollbackFor = [RuntimeException::class, CommException::class])
    fun updateMemberInfo(memberId: Long, adminId: Long?,  memberVM: MemberVM): MemberDTO {
        val memberOpt = memberRepository.findById(memberId)
        val adminOpt = memberRepository.findById(adminId!!)

        if(memberOpt.isEmpty) throw CommException("no member")
        if(adminOpt.isEmpty) throw CommException("no admin")

        val admin = adminOpt.get()
        val member = memberOpt.get()
        val builder = StringBuilder()

        memberVM.kakaoTalkYn?.let {
            if(member.kakaoTalkYn != memberVM.kakaoTalkYn) {
                val beforeAccessYn = if(member.kakaoTalkYn == true) "허용" else "불가"
                val afterAccessYn = if(memberVM.kakaoTalkYn == true) "허용" else "불가"
                builder.append("카톡수신허용 변경 ").append(beforeAccessYn).append(" -> ").append(afterAccessYn).append("\n")
            }
        }


        memberVM.accessYn?.let {
            if(member.accessYn != memberVM.accessYn) {
                if(member.accessYn == false && memberVM.accessYn == true) {
                    kakaoTalkService.sendKakaoTalk(
                        targetMemberDTO = memberMapper.toDto(member),
                        sendPhone = member.phone!!,
                        titleKey = "join.confirm.title",
                        messageKey = "join.confirm.body",
                        templateCodeKey = "join.confirm.template",
                        btnUrlKey = null,
                        btnTextKey = null
                    )
                }
                val beforeAccessYn = if(member.accessYn == true) "허용" else "불가"
                val afterAccessYn = if(memberVM.accessYn == true) "허용" else "불가"
                builder.append("접속허용 변경 ").append(beforeAccessYn).append(" -> ").append(afterAccessYn).append("\n")
            }
        }

        if(!memberVM.emailAddress.isNullOrEmpty()) {
            if(member.emailAddress != memberVM.emailAddress) {
                builder.append("이메일 변경 ").append(member.emailAddress).append(" -> ").append(memberVM.emailAddress).append("\n")
            }
        }
        memberVM.birthDate?.let {
            if(!member.birthDate?.isEqual(memberVM.birthDate)!!) {
                builder.append("생년월일 변경 ").append(member.birthDate).append(" -> ").append(memberVM.birthDate).append("\n")
            }
        }
        if(!memberVM.branch.isNullOrEmpty()) {
            if(member.branch != memberVM.branch) {
                builder.append("지점명 변경 ").append(member.branch).append(" -> ").append(memberVM.branch).append("\n")
            }
        }
        if(!memberVM.manageBranch.isNullOrEmpty()) {
            if(member.manageBranch != memberVM.manageBranch) {
                builder.append("관리지점 변경 ").append(member.manageBranch).append(" -> ").append(memberVM.manageBranch).append("\n")
            }
        }
        if(!memberVM.groupName.isNullOrEmpty()) {
            if(member.groupName != memberVM.groupName) {
                builder.append("그룹 변경 ").append(member.groupName).append(" -> ").append(memberVM.groupName).append("\n")
            }
        }
        if(!memberVM.name.isNullOrEmpty()) {
            if(member.name != memberVM.name) {
                builder.append("이름 변경 ").append(member.name).append(" -> ").append(memberVM.name).append("\n")
            }
        }
        if(!memberVM.phone.isNullOrEmpty()) {
            if(member.phone != memberVM.phone) {
                builder.append("전화번호 변경 ").append(member.phone).append(" -> ").append(memberVM.phone).append("\n")
            }
        }

        memberVM.point?.let {
            if(member.point != memberVM.point) {
                builder.append("포인트 변경 ").append(CommonUtil.comma(member.point?.toInt()!!)).append(" -> ").append(CommonUtil.comma(
                    memberVM.point?.toInt()!!
                )).append("\n")
            }
        }
        if(!memberVM.position.isNullOrEmpty()) {
            if(member.position != memberVM.position) {
                builder.append("직급 변경 ").append(member.position).append(" -> ").append(memberVM.position).append("\n")
            }
        }
        if(!memberVM.roles.isNullOrEmpty()) {
            val beforeRole = if(member.roles.isNullOrEmpty()) {
                "없음"
            } else {
                member.roles?.first()?.description
            }
            if(member.roles?.contains(memberVM.roles?.first()) != true) {

                // 관리자권한은 관리자미만회원에게만 권한 부여 가능
                if(admin.roles?.contains(ROLE.ADMIN)!!) {
                    if(memberVM.roles?.contains(ROLE.ADMIN)!! || memberVM.roles?.contains(ROLE.MASTER)!!) {
                        throw CommException("관리자 미만 권한부여 가능합니다.")
                    }
                }

                val beforeManageBranch = member.manageBranch
                if(member.roles?.contains(ROLE.NEW)!!) {
                    member.updateManageBranch(admin)
                } else if(member.roles?.contains(ROLE.DEALER)!!) {
                    if(admin.roles?.contains(ROLE.ADMIN)!! || admin.roles?.contains(ROLE.MASTER)!!) {
                        member.updateManageBranch(admin)
                    }
                } else if(member.roles?.contains(ROLE.ADMIN)!!) {
                    if(admin.roles?.contains(ROLE.MASTER)!!) {
                        member.updateManageBranch(admin)
                    }
                }
                builder.append("관리지점 변경 ").append(beforeManageBranch).append(" -> ").append(memberVM.manageBranch).append("\n")
                builder.append("권한 변경 ").append(beforeRole).append(" -> ").append(memberVM.roles?.first()?.description).append("\n")
            }
        }

        // 관리지점 설정
        if(member.manageBranch != memberVM.manageBranch) {
            if(admin.roles?.contains(ROLE.ADMIN)!! || admin.roles?.contains(ROLE.MASTER)!!) {
                val beforeManageBranch = member.manageBranch
                member.updateManageBranch(memberVM.manageBranch!!, admin)
                builder.append("관리지점 변경 ").append(beforeManageBranch).append(" -> ").append(memberVM.manageBranch).append("\n")
            } else {
                throw CommException("관리자만 지점 설정 가능합니다.")
            }
        }


        if(!builder.toString().isNullOrEmpty()) {
            val logMessage = member.emailAddress + "회원 " + builder.toString()
            activityLogService.createMemberInfoLog(memberId, adminId, logMessage)
        }
        member.updateMyInfo(memberVM)
        return memberMapper.toDto(memberRepository.save(member))
    }

    @Transactional(rollbackFor = [RuntimeException::class, CommException::class])
    fun updateMemberRole(memberId: Long, adminId: Long, memberVM: MemberVM): MemberDTO {
        val optional = memberRepository.findById(memberId)
        if(optional.isEmpty) throw CommException("no member")

        val stored = optional.get()

        val beforeRole = stored.roles
        val afterRole = memberVM.roles
        stored.roles = memberVM.roles

        activityLogService.createMemberRoleChangeLog(memberId, adminId, beforeRole!!, afterRole!!)
        return memberMapper.toDto(memberRepository.save(stored))
    }


    @Transactional(rollbackFor = [RuntimeException::class, CommException::class])
    fun increasePoint(memberId: Long, increasePoint: Long) {
        val member = memberRepository.findById(memberId).orElseThrow { CommException("not found member") }
        member.increasePoint(increasePoint)
        memberRepository.save(member)
    }

}
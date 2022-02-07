package com.mobilityk.core.service

import com.mobilityk.core.domain.Notification
import com.mobilityk.core.domain.NotificationType
import com.mobilityk.core.domain.TargetType
import com.mobilityk.core.dto.NotificationDTO
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.dto.mapper.NotificationMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.NotificationRepository
import com.mobilityk.core.repository.search.MemberSearchOption
import mu.KotlinLogging
import org.springframework.core.env.Environment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationMapper: NotificationMapper,
    private val kakaoTalkService: KakaoTalkService,
    private val memberRepository: MemberRepository,
    private val environment: Environment,
    private val memberMapper: MemberMapper
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<NotificationDTO> {
        return notificationRepository.findAllByMemberId(memberId, pageable)
            .map { notification ->
                notificationMapper.toDto(notification)
            }
    }

    @Transactional
    fun findById(memberId: Long, notificationId: Long): NotificationDTO {
        val notification = notificationRepository.findById(notificationId).orElseThrow { CommException("not found notification") }
        if(notification.memberId != memberId) throw CommException("no permission")
        notification.readYn = true
        return notificationMapper.toDto(notification)
    }

    @Transactional
    fun sendToAdmins(
        title: String,
        body: String
    ) {
        
    }

    @Transactional
    fun saveAllNotication(title: String, body: String, writerId: Long, noticeId: Long) {
        val searchOption = MemberSearchOption(
            accessYn = true,
            activated = true
            //roles = mutableSetOf(ROLE.NEW, ROLE.DEALER)
        )
        val members = memberRepository.findAllBySearch(searchOption)
        logger.info { "notice target member count ${members.size}" }

        val activeProfiles = environment.activeProfiles
        var isProd = activeProfiles.contains("prod")

        members.let { memberList ->
            memberList.forEach { member ->
                if(isProd) {
                    kakaoTalkService.sendKakaoTalk(
                        targetMemberDTO = memberMapper.toDto(member),
                        sendPhone = member.phone!!,
                        titleKey =  "push.title",
                        messageKey = "push.body",
                        templateCodeKey = "push.template",
                        btnUrlKey = "push.btn.url",
                        btnTextKey = "push.btn.text",
                        urlList = listOf("user/notification/$noticeId")
                    )
                }
                notificationRepository.save(
                    Notification(
                        title = title,
                        memberId = member.id!!,
                        readYn = false,
                        writerId = writerId,
                        body = body,
                        targetType = TargetType.ALL_USER,
                        notificationType = NotificationType.HTML
                    )
                )
            }
        }
    }
}
package com.mobilityk.core.service

import com.mobilityk.core.domain.Notification
import com.mobilityk.core.domain.NotificationType
import com.mobilityk.core.domain.TargetType
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.api.kakao.KakaoTalkResponse
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.NotificationRepository
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.Locale

@Service
data class KakaoTalkService(
    private val notificationRepository: NotificationRepository,
    private val restTemplate: RestTemplate,
    private val messageSource: MessageSource,
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper,
    @Value("\${kakao.talk.client-id}")
    private val talkClientId: String,
    @Value("\${kakao.talk.key}")
    private val talkKey: String,
    @Value("\${kakao.talk.request-url}")
    private val talkRequestUrl: String,
    @Value("\${kakao.talk.api-version}")
    private val talkApiVersion: String,
    @Value("\${kakao.talk.callback-number}")
    private val callBackNumber: String,
) {

    private val logger = KotlinLogging.logger {}

    private fun getHeader(accessToken: String?): HttpHeaders {
        var headers = HttpHeaders()
        accessToken?.let {
            headers.add("Authorization" , "Bearer $accessToken")
        }
        headers.add("Content-Type" , "application/x-www-form-urlencoded")
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        return headers
    }

    @Transactional
    fun registerCallBackNumber(callBackNumber: String, pinCode: String?) {
        val parameter = LinkedMultiValueMap<String, Any>()
        val headers = getHeader(null)
        headers.add("x-waple-authorization", talkKey)

        parameter.add("sendnumber", callBackNumber)
        parameter.add("comment", "발신번호등록")
        parameter.add("pintype", "SMS")
        pinCode?.let {
            parameter.add("pincode", it)
        }
        val formEntity = HttpEntity<MultiValueMap<String, Any>>(parameter , headers)
        val responseEntity = restTemplate.exchange(
            "$talkRequestUrl/$talkApiVersion/sendnumber/save/$talkClientId"
            , HttpMethod.POST
            , formEntity
            , KakaoTalkResponse::class.java
        )
        logger.info { "kakao talk push response ${responseEntity.body}" }
    }

    @Transactional
    @Async
    fun sendKakaoTalkToAdmin(
        memberDTO: MemberDTO,
        titleKey: String,
        messageKey: String,
        templateCodeKey: String,
        btnUrlKey: String?,
        btnTextKey: String?,
        messageList: List<String>? = null,
        urlList: List<String>? = null,
    ): Unit {

        memberDTO.manageBranch?.let { manageBranch ->
            val admins = memberRepository.findAllByRolesInAndBranch(listOf(ROLE.ADMIN), manageBranch)
            if(!admins.isNullOrEmpty()) {
                admins.forEach { admin ->
                    admin.phone?.let {
                        sendKakaoTalk(
                            targetMemberDTO = memberMapper.toDto(admin),
                            titleKey = titleKey,
                            sendPhone = admin.phone!!,
                            messageKey = messageKey,
                            templateCodeKey = templateCodeKey,
                            btnUrlKey = btnUrlKey,
                            btnTextKey = btnTextKey,
                            messageList = messageList,
                            urlList = urlList
                        )
                    }
                }
            }
        }

        val masters = memberRepository.findAllByRolesIn(listOf(ROLE.MASTER))
        if(!masters.isNullOrEmpty()) {
            masters.forEach { master ->
                master.phone?.let {
                    sendKakaoTalk(
                        targetMemberDTO = memberMapper.toDto(master),
                        titleKey = titleKey,
                        sendPhone = master.phone!!,
                        messageKey = messageKey,
                        templateCodeKey = templateCodeKey,
                        btnUrlKey = btnUrlKey,
                        btnTextKey = btnTextKey,
                        messageList = messageList,
                        urlList = urlList
                    )
                }
            }
        }
    }


    @Transactional
    @Async
    fun sendKakaoTalk(
        targetMemberDTO: MemberDTO,
        sendPhone: String,
        titleKey: String,
        messageKey: String,
        templateCodeKey: String,
        btnUrlKey: String?,
        btnTextKey: String?,
        messageList: List<String>? = null,
        urlList: List<String>? = null,
    ) : KakaoTalkResponse? {

        if(targetMemberDTO.kakaoTalkYn == null || !targetMemberDTO.kakaoTalkYn!!) {
            return null
        }

        val parameter = LinkedMultiValueMap<String, Any>()
        val headers = getHeader(null)
        headers.add("x-waple-authorization", talkKey)

        val title = messageSource.getMessage(titleKey, emptyArray(), Locale.KOREA)
        val message = messageSource.getMessage(messageKey, messageList?.toTypedArray(), Locale.KOREA)
        val templateCode = messageSource.getMessage(templateCodeKey, emptyArray<String>(), Locale.KOREA)
        val btnUrl = if(!btnUrlKey.isNullOrEmpty()) messageSource.getMessage(btnUrlKey, urlList?.toTypedArray(), Locale.KOREA) else null
        val btnText =  if(!btnTextKey.isNullOrEmpty()) messageSource.getMessage(btnTextKey, emptyArray<String>(), Locale.KOREA) else null

        logger.info { "kakao push send number [$sendPhone]" }
        //logger.info { "kakao push callback number [$callBackNumber]" }
        logger.info { "kakao push callback number [01022344573]" }
        logger.info { "kakao push message [$message]" }
        logger.info { "kakao push templateCode [$templateCode]" }

        parameter.add("PHONE", sendPhone)
        parameter.add("CALLBACK", "01022344573")
        parameter.add("MSG", message)
        parameter.add("TEMPLATE_CODE", templateCode)
        parameter.add("FAILED_TYPE", "N")
        if(btnUrl.isNullOrEmpty()) {
            parameter.add("BTN_TYPES", "N")
        } else {
            parameter.add("BTN_TYPES", "웹링크")
        }
        if(btnText.isNullOrEmpty()) {
            parameter.add("BTN_TXTS", "N")
        } else {
            parameter.add("BTN_TXTS", btnText)
        }
        if(btnUrl.isNullOrEmpty()) {
            parameter.add("BTN_URLS1", "N")
        } else {
            parameter.add("BTN_URLS1", btnUrl)
        }

        val formEntity = HttpEntity<MultiValueMap<String, Any>>(parameter , headers)
        val responseEntity = restTemplate.exchange(
            "$talkRequestUrl/$talkApiVersion/msg/$talkClientId"
            , HttpMethod.POST
            , formEntity
            , KakaoTalkResponse::class.java
        )
        logger.info { "kakao talk push response ${responseEntity.body}" }

        notificationRepository.save(
            Notification(
                memberId = targetMemberDTO.id,
                writerId = targetMemberDTO.id,
                body = message,
                title = title,
                readYn = false,
                targetType = TargetType.SPECIPIC_USER,
                notificationType = NotificationType.TEXT
            )
        )

        return responseEntity.body!!
    }
}
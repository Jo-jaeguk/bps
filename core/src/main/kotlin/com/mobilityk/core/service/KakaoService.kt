package com.mobilityk.core.service

import com.mobilityk.core.dto.api.kakao.KakaoResponse
import com.mobilityk.core.dto.api.kakao.KakaoTalkResponse
import com.mobilityk.core.dto.api.kakao.KakaoUserInfo
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
data class KakaoService(
    private val restTemplate: RestTemplate,
    private val messageSource: MessageSource,
    @Value("\${kakao.rest-api.key}")
    private val restApiKey: String,
    @Value("\${kakao.join.redirect-url}")
    private val joinRedirectURl: String,
    @Value("\${kakao.login.redirect-url}")
    private val loginRedirectURl: String,
    @Value("\${kakao.admin.redirect-url}")
    private val adminLoginRedirectUrl: String,
    @Value("\${kakao.secret-key}")
    private val secretKey: String,
    @Value("\${kakao.auth.request-url}")
    private val authRequestUrl: String,
    @Value("\${kakao.api.request-url}")
    private val apiRequestUrl: String,
    @Value("\${kakao.talk.client-id}")
    private val talkClientId: String,
    @Value("\${kakao.talk.key}")
    private val talkKey: String,
    @Value("\${kakao.talk.request-url}")
    private val talkRequestUrl: String,
    @Value("\${kakao.talk.api-version}")
    private val talkApiVersion: String,
) {
    private val logger = KotlinLogging.logger {}

    private fun getHeader(accessToken: String?): HttpHeaders {
        var headers = HttpHeaders()
        accessToken?.let {
            headers.add("Authorization" , "Bearer $accessToken")
        }
        headers.add("Content-Type" , "application/json")
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        return headers
    }

    fun getAccessToken(code: String, type: String, isUser: Boolean): KakaoResponse {
        val parameter = LinkedMultiValueMap<String, Any>()

        parameter.add("grant_type","authorization_code")
        parameter.add("client_id", restApiKey)

        if(isUser) {
            if(type == "login") {
                parameter.add("redirect_uri", loginRedirectURl)
            } else {
                parameter.add("redirect_uri", joinRedirectURl)
            }
        } else {
            parameter.add("redirect_uri", adminLoginRedirectUrl)
        }

        parameter.add("code", code)
        parameter.add("client_secret", secretKey)

        val formEntity = HttpEntity<MultiValueMap<String, Any>>(parameter , getHeader(null))
        val responseEntity = restTemplate.exchange(
            authRequestUrl
            , HttpMethod.POST
            , formEntity
            , KakaoResponse::class.java
        )
        logger.info { "kakao response ${responseEntity.body}" }
        return responseEntity.body!!
    }

    fun getUserInfo(kakaoResponse: KakaoResponse): KakaoUserInfo {
        val parameter = LinkedMultiValueMap<String, Any>()

        val formEntity = HttpEntity<MultiValueMap<String, Any>>(parameter , getHeader(kakaoResponse.access_token))
        val responseEntity = restTemplate.exchange(
            apiRequestUrl
            , HttpMethod.POST
            , formEntity
            , KakaoUserInfo::class.java
        )
        logger.info { "kakao userInfo ${responseEntity.body}" }
        return responseEntity.body!!
    }

    fun pushKakaoTalk(phone: String, message: String) {

    }

}
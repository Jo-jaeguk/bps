package com.mobilityk.core.service

import com.mobilityk.core.dto.api.kakao.KakaoResponse
import com.mobilityk.core.dto.api.kakao.KakaoTalkResponse
import com.mobilityk.core.dto.api.kakao.KakaoUserInfo
import com.mobilityk.core.dto.api.naver.NaverResponse
import com.mobilityk.core.dto.api.naver.NaverUserInfo
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
data class NaverService(
    private val restTemplate: RestTemplate,
    private val messageSource: MessageSource,
    @Value("\${naver.client-id}")
    private val clientId: String,
    @Value("\${naver.client-secret}")
    private val clientSecret: String,
    @Value("\${naver.auth.request-url}")
    private val authRequestUrl: String,
    @Value("\${naver.api.request-url}")
    private val apiRequestUrl: String,

) {
    private val logger = KotlinLogging.logger {}

    private fun getHeader(accessToken: String?): HttpHeaders {
        var headers = HttpHeaders()
        accessToken?.let {
            headers.add("Authorization" , "Bearer $accessToken")
        }
        headers.add("Content-Type" , "application/json")
        //headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        return headers
    }

    fun getAccessToken(code: String, state: String): NaverResponse {

        val requestUrl = StringBuilder()
        requestUrl.append(authRequestUrl)
            .append("?")
            .append("grant_type").append("=").append("authorization_code").append("&")
            .append("client_id").append("=").append(clientId).append("&")
            .append("client_secret").append("=").append(clientSecret).append("&")
            .append("code").append("=").append(code).append("&")
            .append("state").append("=").append(state).append("&")

        //val formEntity = HttpEntity<MultiValueMap<String, Any>>(parameter , getHeader(null))
        val responseEntity = restTemplate.exchange(
            requestUrl.toString()
            , HttpMethod.GET
            , HttpEntity<String>(getHeader(null))
            , NaverResponse::class.java
        )
        logger.info { "naver response ${responseEntity.body}" }
        return responseEntity.body!!
    }

    fun getUserInfo(naverResponse: NaverResponse): NaverUserInfo {
        //val parameter = LinkedMultiValueMap<String, Any>()

        //val formEntity = HttpEntity<MultiValueMap<String, Any>>(parameter , getHeader(kakaoResponse.access_token))
        val responseEntity = restTemplate.exchange(
            apiRequestUrl
            , HttpMethod.GET
            , HttpEntity<String>(getHeader(naverResponse.access_token))
            , NaverUserInfo::class.java
        )
        logger.info { "naver userInfo ${responseEntity.body}" }
        return responseEntity.body!!
    }

    fun pushKakaoTalk(phone: String, message: String) {

    }

}
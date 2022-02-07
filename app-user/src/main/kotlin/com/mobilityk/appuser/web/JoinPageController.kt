package com.mobilityk.appuser.web

import com.mobilityk.core.service.MemberService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import kotlin.random.Random

@Controller
@RequestMapping("/join")
data class JoinPageController(
    private val memberService: MemberService,
    @Value("\${naver.client-id}")
    private val naverClientId: String,
    @Value("\${naver.join.redirect-url}")
    private val naverRedirectUrl: String,
    @Value("\${kakao.join.redirect-url}")
    private val kakaoRedirectUrl: String,
) {

    @GetMapping("")
    fun join(
        model: MutableMap<String, Any>
    ): ModelAndView {
        model["naverClientId"] = naverClientId
        model["naverRedirectUrl"] = naverRedirectUrl
        model["kakaoRedirectUrl"] = kakaoRedirectUrl
        model["naver_state"] = Random.nextInt(1000).toString()
        return ModelAndView("join/join" , model)
    }

    @GetMapping("/email")
    fun joinEmail(
        model: MutableMap<String, Any>
    ): ModelAndView {
        return ModelAndView("join/join_email" , model)
    }

    @GetMapping("/term")
    fun term(
        model: MutableMap<String, Any>
    ): ModelAndView {
        return ModelAndView("term/term" , model)
    }

}
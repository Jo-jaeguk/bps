package com.mobilityk.appadmin

import com.mobilityk.core.service.MemberService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import kotlin.random.Random

@Controller
data class HomeController(
    private val memberService: MemberService,
    @Value("\${naver.client-id}")
    private val naverClientId: String,
    @Value("\${naver.admin.redirect-url}")
    private val naverRedirectUrl: String,
    @Value("\${kakao.admin.redirect-url}")
    private val kakaoRedirectUrl: String,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/")
    fun home(model: MutableMap<String, Any>): ModelAndView {
        return ModelAndView("redirect:/admin/dashboard" , model)
    }

    @GetMapping("/main")
    fun main(
        model: MutableMap<String, Any>,
        request: HttpServletRequest
    ): ModelAndView {
        logger.info { "/main servletPath url ${request.servletPath}" }
        return ModelAndView("redirect:/admin/dashboard" , model)
    }

    @GetMapping("/login")
    fun login(
        model: MutableMap<String, Any>,
        request: HttpServletRequest,
        httpSession: HttpSession
    ): ModelAndView {

        model["naverClientId"] = naverClientId
        model["naverRedirectUrl"] = naverRedirectUrl
        model["kakaoRedirectUrl"] = kakaoRedirectUrl
        model["naver_state"] = Random.nextInt(1000).toString()

        return ModelAndView("login/login" , model)
    }
}
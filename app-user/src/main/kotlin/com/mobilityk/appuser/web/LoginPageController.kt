package com.mobilityk.appuser.web

import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.NiceAuthService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession
import kotlin.random.Random

@Controller
@RequestMapping("/login")
data class LoginPageController(
    private val memberService: MemberService,
    @Value("\${naver.client-id}")
    private val naverClientId: String,
    @Value("\${naver.login.redirect-url}")
    private val naverRedirectUrl: String,
    @Value("\${kakao.login.redirect-url}")
    private val kakaoRedirectUrl: String,
    private val niceAuthService: NiceAuthService
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("")
    fun login(
        @RequestParam("error" , required = false) error: String?,
        model: MutableMap<String, Any>,
        @AuthenticationPrincipal user: User?
    ): ModelAndView {
        model["naverClientId"] = naverClientId
        model["naverRedirectUrl"] = naverRedirectUrl
        model["kakaoRedirectUrl"] = kakaoRedirectUrl
        model["naver_state"] = Random.nextInt(1000).toString()

        return if(user != null && !user.username.isNullOrEmpty()) {
            ModelAndView("redirect:/user/home" , model)
        } else {
            if(!error.isNullOrEmpty()) {
                return ModelAndView("redirect:/login/email?error=$error" , model)
            } else {
                return ModelAndView("login/login" , model)
            }
        }
    }

    @GetMapping("/email")
    fun loginEmail(
        @RequestParam("error" , required = false) error: String?,
        @RequestParam("success" , required = false) success: String?,
        model: MutableMap<String, Any>,
        @AuthenticationPrincipal user: User?
    ): ModelAndView {
        return if(user != null && !user.username.isNullOrEmpty()) {
            ModelAndView("redirect:/user/home" , model)
        } else {
            model["error"] = error ?: ""
            model["success"] = success ?: ""
            ModelAndView("login/email" , model)
        }
    }

    @GetMapping("/naver")
    fun loginNaver(model: MutableMap<String, Any>): ModelAndView {
        return ModelAndView("login/email" , model)
    }

    @GetMapping("/kakao")
    fun loginKakao(model: MutableMap<String, Any>): ModelAndView {
        return ModelAndView("login/login" , model)
    }

    @GetMapping("/search/password")
    fun searchPassword(
        @RequestParam("memberId", required = false) memberId: Long?,
        @RequestParam("result", required = false) result: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        if(memberId == null && result.isNullOrEmpty()) {
            val encData = niceAuthService.getSearchEncData(httpSession, "PW")
            logger.info { "encData" }
            logger.info { "$encData" }
            model["encData"] = encData
            val url = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?EncodeData=$encData&m=checkplusService"
            //return ModelAndView("nice/auth" , model)
            return ModelAndView("redirect:$url" , model)
        } else {
            model["memberId"] = memberId.toString()
            model["result"] = result.toString()
            return ModelAndView("login/search_password" , model)
        }
    }


    @GetMapping("/search/id")
    fun searchId(
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        val encData = niceAuthService.getSearchEncData(httpSession, "ID")
        logger.info { "encData" }
        logger.info { "$encData" }
        model["encData"] = encData
        val url = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?EncodeData=$encData&m=checkplusService"
        //return ModelAndView("nice/auth" , model)
        return ModelAndView("redirect:$url" , model)
    }
}
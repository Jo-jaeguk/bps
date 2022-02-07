package com.mobilityk.appuser.web

import com.mobilityk.core.exception.AlreadyExistUserException
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.service.KakaoService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.SecurityLoginService
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/kakao")
data class KakaoPageController(
    private val kakaoService: KakaoService,
    private val memberService: MemberService,
    private val securityLoginService: SecurityLoginService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/oauth/user/join")
    fun kakaoOauthJoin(
        @RequestParam("code", required = false) code: String?,
        @RequestParam("access_denied", required = false) access_denied: String?,
        @RequestParam("login_required", required = false) login_required: String?,
        @RequestParam("consent_required", required = false) consent_required: String?,
        @RequestParam("interaction_required", required = false) interaction_required: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "code : $code" }
        logger.info { "access_denied : $access_denied" }
        logger.info { "login_required : $login_required" }
        logger.info { "consent_required : $consent_required" }
        logger.info { "interaction_required : $interaction_required" }
        val kakaoResponse = kakaoService.getAccessToken(code!!, "join", true)
        val kakaoUserInfo = kakaoService.getUserInfo(kakaoResponse)
        try {
            val kakaoId = memberService.joinByKakao(kakaoUserInfo)
            model["success"] = "join success"
            return ModelAndView("redirect:/nice/auth?accountType=KAKAO&id=$kakaoId")
        } catch (e: AlreadyExistUserException) {
            model["error"] = "already exist member"
            return ModelAndView("join/join" , model)
        }
        //return ModelAndView("join/join", model)
    }


    @GetMapping("/oauth/user/login")
    fun kakaoOauthLogin(
        @RequestParam("code", required = false) code: String?,
        @RequestParam("access_denied", required = false) access_denied: String?,
        @RequestParam("login_required", required = false) login_required: String?,
        @RequestParam("consent_required", required = false) consent_required: String?,
        @RequestParam("interaction_required", required = false) interaction_required: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "code : $code" }
        logger.info { "access_denied : $access_denied" }
        logger.info { "login_required : $login_required" }
        logger.info { "consent_required : $consent_required" }
        logger.info { "interaction_required : $interaction_required" }
        val kakaoResponse = kakaoService.getAccessToken(code!!, "login", true)
        val kakaoUserInfo = kakaoService.getUserInfo(kakaoResponse)
        try {
            val memberDTO = memberService.loginByKakao(kakaoUserInfo)
            /*
            model["member_email"] = memberDTO.emailAddress!!
            model["member_password"] = "1234"
            return ModelAndView("login/login" , model)
             */

            val securityMember = securityLoginService.loadUserByUsername(memberDTO.emailAddress)
            securityMember?.let {
                val authentication = UsernamePasswordAuthenticationToken(it, it.password, it.authorities)
                val securityContext = SecurityContextHolder.getContext()
                securityContext.authentication = authentication
                httpSession.setAttribute("SPRING_SECURITY_CONTEXT" , securityContext)
                return ModelAndView("redirect:/")
            }

        } catch (e: CommException) {
            model["error"] = e.message!!
            return ModelAndView("login/login" , model)
        }
        return ModelAndView("redirect:/")
    }
}
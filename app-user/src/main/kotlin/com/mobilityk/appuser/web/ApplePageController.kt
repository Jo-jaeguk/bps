package com.mobilityk.appuser.web

import com.mobilityk.core.exception.AlreadyExistUserException
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.service.AppleService
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
@RequestMapping("/apple")
data class ApplePageController(
    private val appleService: AppleService,
    private val memberService: MemberService,
    private val securityLoginService: SecurityLoginService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/oauth/user/join")
    fun appleJoin(
        @RequestParam("uid", required = true) uid: String,
        @RequestParam("email", required = false) email: String?,
        @RequestParam("name", required = false) name: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "uid : $uid" }
        logger.info { "email : $email" }
        try {
            val appleId = memberService.joinByApple(uid, name, email)
            model["success"] = "join success"
            return ModelAndView("redirect:/nice/auth?accountType=APPLE&id=$appleId")
        } catch (e: AlreadyExistUserException) {
            model["error"] = "already exist member"
            return ModelAndView("join/join" , model)
        }
        //return ModelAndView("join/join", model)
    }


    @GetMapping("/oauth/user/login")
    fun appleLogin(
        @RequestParam("uid", required = true) uid: String,
        @RequestParam("email", required = false) email: String?,
        @RequestParam("name", required = false) name: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {

        try {
            val memberDTO = memberService.loginByApple(uid)
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
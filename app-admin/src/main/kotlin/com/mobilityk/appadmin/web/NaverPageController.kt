package com.mobilityk.appadmin.web

import com.mobilityk.core.exception.AlreadyExistUserException
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.NaverService
import com.mobilityk.core.service.SecurityLoginService
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/naver")
data class NaverPageController(
    private val memberService: MemberService,
    private val securityLoginService: SecurityLoginService,
    private val naverService: NaverService
) {
    private val logger = KotlinLogging.logger {}



    @GetMapping("/oauth/user/login")
    fun naverOauthLogin(
        @RequestParam("code", required = false) code: String?,
        @RequestParam("state", required = false) state: String?,
        @RequestParam("error", required = false) error: String?,
        @RequestParam("error_description", required = false) error_description: String?,
        model: MutableMap<String, Any>,
        httpSession: HttpSession
    ): ModelAndView {
        logger.info { "code : $code" }
        logger.info { "state : $state" }
        logger.info { "error : $error" }
        logger.info { "error_description : $error_description" }

        val naverResponse = naverService.getAccessToken(code!!, state!!)
        val naverUserInfo = naverService.getUserInfo(naverResponse)
        try {
            val memberDTO = memberService.loginByNaver(naverUserInfo)
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

                // admin 이면 세션 유효기간은 해당일자 자정 까지 로 한다.
                val now = LocalDateTime.now()
                val endOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.of(23,59,59))
                val betweenSecond = ChronoUnit.SECONDS.between(now, endOfDay)
                logger.info { "시간 차이 $betweenSecond" }
                httpSession.maxInactiveInterval = betweenSecond.toInt()

                return ModelAndView("redirect:/")
            }

        } catch (e: CommException) {
            model["error"] = e.message!!
            return ModelAndView("redirect:/login" , model)
        }

        return ModelAndView("redirect:/")
    }
}
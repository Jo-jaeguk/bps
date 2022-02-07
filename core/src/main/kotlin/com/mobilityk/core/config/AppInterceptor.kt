package com.mobilityk.core.config

import com.mobilityk.core.domain.Member
import com.mobilityk.core.service.MemberService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AppInterceptor : HandlerInterceptor {

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var memberService: MemberService

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        val requestURI = request.requestURI
        if(requestURI.startsWith("/admin") ||
            requestURI.startsWith("/api") ||
            requestURI.startsWith("/user")) {
            /*
            val securityContext = SecurityContextHolder.getContext()
            val authentication = securityContext.authentication
            logger.info { "interceptor check" }
            if(authentication != null) {

                val session = request.getSession(false)
                val memberName = session.getAttribute("name")
                logger.info { "memberName $memberName" }

                if(requestURI.startsWith("/user")) {
                    val user = authentication.principal as User
                    val member = memberService.findById(user.username.toLong())
                    if(member.phone.isNullOrEmpty()) {
                        modelAndView?.viewName = "redirect:/nice/auth"
                    }
                }
            } else {
                modelAndView?.viewName = "redirect:/logout"
            }
            */
        }
        super.postHandle(request, response, handler, modelAndView)
    }
}
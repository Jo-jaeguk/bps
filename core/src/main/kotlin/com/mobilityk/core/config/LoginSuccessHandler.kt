package com.mobilityk.core.config

import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoginSuccessHandler: AuthenticationSuccessHandler {

    private val logger = KotlinLogging.logger {}
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val pathUri = request?.servletPath
        logger.info { "LoginSuccessHandler !! path $pathUri" }

        val session = request?.session
        session?.let { httpSession ->
            val prevPage = httpSession.getAttribute("prevPage")
            logger.info { "LoginSuccessHandler prevPage $prevPage" }
            pathUri?.let { uri ->

                authentication?.let { authentication ->
                    authentication.authorities.forEach { grant ->
                        logger.info { "role ${grant.authority}" }
                        if(grant.authority.contains("MASTER") || grant.authority.contains("ADMIN")) {
                            // admin 이면 세션 유효기간은 해당일자 자정 까지 로 한다.
                            val now = LocalDateTime.now()
                            val endOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.of(23,59,59))
                            val betweenSecond = ChronoUnit.SECONDS.between(now, endOfDay)
                            logger.info { "시간 차이 $betweenSecond" }
                            request.session.maxInactiveInterval = betweenSecond.toInt()
                        }
                    }
                }

                if(uri.startsWith("/user")) {
                    request.session.maxInactiveInterval = 60 * 60 * 24 * 30
                } else {

                    val redirectStrategy: DefaultRedirectStrategy = DefaultRedirectStrategy()
                    val requestCache = HttpSessionRequestCache()
                    val savedRequest = requestCache.getRequest(request, response)
                    if(savedRequest != null) {
                        val redirectUrl = savedRequest.redirectUrl
                        if(!redirectUrl.isNullOrEmpty()) {
                            logger.info { "LoginSuccessHandler redirect Url $redirectUrl" }
                            redirectStrategy.sendRedirect(request, response, redirectUrl)
                        } else {
                            response?.sendRedirect("/")
                        }
                    } else {
                        response?.sendRedirect("/")
                    }
                }
            }
        }

    }
}
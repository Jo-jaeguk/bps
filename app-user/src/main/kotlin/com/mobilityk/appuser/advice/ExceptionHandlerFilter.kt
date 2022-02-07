package com.mobilityk.appuser.advice

import mu.KotlinLogging
import org.springframework.security.web.authentication.rememberme.CookieTheftException
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ExceptionHandlerFilter: GenericFilterBean() {
    private val logger = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        logger.info("ExceptionHandlerFilter !!!")
        //val req = request as HttpServletRequest
        val res = response as HttpServletResponse
        try {
            chain?.doFilter(request, response)
        } catch (e: CookieTheftException) {
            logger.info("CookieTheftException !!!")
            res.sendRedirect("/logout")
        }
    }
}
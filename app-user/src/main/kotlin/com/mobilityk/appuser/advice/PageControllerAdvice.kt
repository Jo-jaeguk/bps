package com.mobilityk.appuser.advice

import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.security.web.authentication.rememberme.CookieTheftException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ControllerAdvice(basePackages = ["com.mobilityk.appuser.web"])
class PageControllerAdvice {
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(value = [CookieTheftException::class])
    fun invalidRememberMe(
        e: CookieTheftException,
        request: HttpServletRequest,
        response: HttpServletResponse
    )  {
        logger.error { "CookieTheftException " + e.message }
        logger.error { "CookieTheftException " + CommonUtil.getExceptionPrintStack(e) }
        response.sendRedirect("/logout")
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun runtime(
        e: CookieTheftException,
        request: HttpServletRequest,
        response: HttpServletResponse
    )  {
        logger.error { "RuntimeException " + e.message }
        logger.error { "RuntimeException " + CommonUtil.getExceptionPrintStack(e) }
        response.sendRedirect("/logout")
    }
}
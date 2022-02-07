package com.mobilityk.core.config

import mu.KotlinLogging
import org.springframework.stereotype.Service
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class CookieUtil {

    private val logger = KotlinLogging.logger {}
    fun createCookie(name: String, value: String, validationSecond: Int): Cookie {
        val tokenCookie = Cookie(name, value)
        tokenCookie.isHttpOnly = true
        logger.info { "cookie validationSecond $validationSecond" }
        //tokenCookie.maxAge = validationSecond
        tokenCookie.maxAge = 60 * 60 * 24 * 60
        tokenCookie.path = "/"
        return tokenCookie
    }
    fun getCookie(req: HttpServletRequest, cookieName: String): Cookie? {
        val cookies = req.cookies ?: return null
        cookies.forEach { cookie ->
            if(cookie.name == cookieName) {
                return cookie
            }
        }
        return null
    }

    fun removeCookie(req: HttpServletRequest, res: HttpServletResponse, cookieName: String) {
        val cookies = req.cookies
        cookies.forEach { cookie ->
            if(cookie.name == cookieName) {
                cookie.maxAge = 0
                res.addCookie(cookie)
            }
        }
    }
}
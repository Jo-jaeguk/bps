package com.mobilityk.core.config

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAccessDeniedHandler: AccessDeniedHandler {
    private val logger = KotlinLogging.logger {}
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?
    ) {
        response?.status = HttpStatus.FORBIDDEN.value()
        val path = request?.servletPath
        path?.let {
            if(it.startsWith("/admin")) {
                request.setAttribute("message", "권한이 없습니다.")
                request.setAttribute("nextUrl" , it)
                val prevPage = request.getHeader("REFERER")
                request.setAttribute("prevPage" , prevPage)
                request.getRequestDispatcher("/error/page/403").forward(request, response)
            } else {
                request.setAttribute("message", "권한이 없습니다.")
                request.setAttribute("nextUrl" , it)
                request.getRequestDispatcher("/error/body/403").forward(request, response)
            }
        }
    }
}
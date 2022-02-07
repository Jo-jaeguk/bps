package com.mobilityk.core.config

import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
data class JwtAuthenticationEntryPoint(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberService: MemberService,
    private val memberMapper: MemberMapper,
    private val cookieUtil: CookieUtil
): AuthenticationEntryPoint {

    private val logger = KotlinLogging.logger {}

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        logger.error { "AuthenticationEntryPoint commence" }
        logger.error { "request request.servletPath ${request?.servletPath}" }

        val path = request?.servletPath
        path?.let {
            if(it.startsWith("/admin") || it.startsWith("/user")) {
                try {
                    // access token in valid 상태면.. refresh token 으로 유효성 체크하고 토큰재발급
                    val refreshToken = jwtTokenProvider.resolveRefreshToken(request)
                    if(!refreshToken.isNullOrEmpty() && jwtTokenProvider.validateToken(refreshToken)) {
                        val member = memberService.findByRefreshToken(refreshToken)
                        val newJwtToken = jwtTokenProvider.createJwtTokenDTO(member.emailAddress!!, memberMapper.toDto(member))
                        memberService.updateRefreshToken(member.id!!, newJwtToken.refreshToken!!)
                        val accessTokenCookie = cookieUtil.createCookie(
                            JwtTokenProvider.ACCESS_TOKEN_NAME, newJwtToken.accessToken!!, TimeUnit.MILLISECONDS.toSeconds(
                                JwtTokenProvider.ACCESS_TOKEN_VALID_TIME
                            ).toInt())
                        val refreshTokenCookie = cookieUtil.createCookie(
                            JwtTokenProvider.REFRESH_TOKEN_NAME,
                            newJwtToken.refreshToken, TimeUnit.MILLISECONDS.toSeconds(JwtTokenProvider.REFRESH_TOKEN_VALID_TIME).toInt())
                        response?.addCookie(accessTokenCookie)
                        response?.addCookie(refreshTokenCookie)
                    } else {
                        logger.error("refresh token is null or invalid")
                        response?.sendRedirect("/logout")
                    }
                } catch (e: Exception) {
                    logger.info { "admin JwtAuthenticationEntryPoint Exception" }
                    logger.info { CommonUtil.getExceptionPrintStack(e) }
                    response?.sendRedirect("/logout")
                }

            } else {
                logger.info { "path is not started /admin and /user" }
                response?.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauth")
            }
        }
        //response?.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauth")
    }
}
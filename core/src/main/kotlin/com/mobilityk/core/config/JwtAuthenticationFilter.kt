package com.build.core.config

import com.mobilityk.core.config.CookieUtil
import com.mobilityk.core.config.JwtTokenProvider
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.TimeUnit
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
data class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val cookieUtil: CookieUtil,
    private val memberMapper: MemberMapper,
    private val memberService: MemberService
): OncePerRequestFilter() {
    private val logger = KotlinLogging.logger {}
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val servletPath = request.servletPath
        try {
            if(servletPath.startsWith("/admin") ||
                servletPath.startsWith("/user") ||
                servletPath.startsWith("/api")) {

                logger.info("start doFilterInternal servlet path : $servletPath")
                val accessToken = jwtTokenProvider.resolveAccessToken(request)
                logger.info("accessToken [$accessToken]")
                if(!accessToken.isNullOrEmpty() && jwtTokenProvider.validateToken(accessToken)) {
                    acceptSecurityUser(accessToken)
                } else {
                    logger.info("access token is null or expired")
                    //refreshToken(request, response, servletPath)
                }
            }
        } catch (e: Exception) {
            logger.info("OncePerRequestFilter Exception ")
            logger.info(CommonUtil.getExceptionPrintStack(e))
            //refreshToken(request, response, servletPath)
        }
        filterChain.doFilter(request, response)
    }

    private fun refreshToken(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val refreshToken = jwtTokenProvider.resolveRefreshToken(request)
            logger.error("refreshToken [$refreshToken]")
            if(!refreshToken.isNullOrEmpty() && jwtTokenProvider.validateToken(refreshToken)) {
                val member = memberService.findByRefreshToken(refreshToken)
                val newJwtToken = jwtTokenProvider.createJwtTokenDTO(member.emailAddress!!, memberMapper.toDto(member))
                memberService.updateRefreshToken(member.id!!, newJwtToken.refreshToken!!)

                val accessTokenCookie = cookieUtil.createCookie(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, newJwtToken.accessToken!!, TimeUnit.MILLISECONDS.toSeconds(
                        JwtTokenProvider.ACCESS_TOKEN_VALID_TIME).toInt())
                val refreshTokenCookie = cookieUtil.createCookie(
                    JwtTokenProvider.REFRESH_TOKEN_NAME,
                    newJwtToken.refreshToken, TimeUnit.MILLISECONDS.toSeconds(JwtTokenProvider.REFRESH_TOKEN_VALID_TIME).toInt())

                logger.error("remove Cookie")
                cookieUtil.removeCookie(request, response, JwtTokenProvider.ACCESS_TOKEN_NAME)
                cookieUtil.removeCookie(request, response, JwtTokenProvider.REFRESH_TOKEN_NAME)

                response.addCookie(accessTokenCookie)
                response.addCookie(refreshTokenCookie)

                acceptSecurityUser(newJwtToken.accessToken)

            } else {
                logger.error("refresh token is null or invalid")
            }
        } catch (e: Exception) {
            logger.error("refresh token is not valid")
        }
    }


    private fun acceptSecurityUser(accessToken: String) {
        val userDetails = jwtTokenProvider.getAuthentication(accessToken)
        SecurityContextHolder.getContext().authentication = userDetails
    }

}
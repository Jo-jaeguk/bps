package com.mobilityk.appadmin.web.api

import com.build.core.dto.common.RefreshTokenDTO
import com.mobilityk.core.config.CookieUtil
import com.mobilityk.core.config.JwtTokenProvider
import com.mobilityk.core.dto.api.LoginReqDTO
import com.mobilityk.core.dto.common.JwtTokenDTO
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.SecurityLoginService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/token")
data class JwtTokenRestController(
    private val memberService: MemberService,
    private val memberMapper: MemberMapper,
    private val jwtTokenProvider: JwtTokenProvider,
    private val cookieUtil: CookieUtil,
    private val securityLoginService: SecurityLoginService,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/exchange")
    fun token(
        @RequestBody loginReqDTO: LoginReqDTO,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<JwtTokenDTO> {
        var jwtTokenDTO: JwtTokenDTO?

        val member = memberService.findByEmailAddress(loginReqDTO.emailAddress!!)
        if(!passwordEncoder.matches(loginReqDTO.password , member.password)) {
            throw BadCredentialsException("ID/PW 를 확인해주세요")
        }

        logger.info { "member loginId ${member.emailAddress} ${member.roles}" }
        jwtTokenDTO = jwtTokenProvider.createJwtTokenDTO(member.emailAddress!!, memberMapper.toDto(member))
        memberService.updateRefreshToken(member.id!!, jwtTokenDTO.refreshToken!!)

        val accessTokenCookie = cookieUtil.createCookie(JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenDTO.accessToken!!, TimeUnit.MILLISECONDS.toSeconds(JwtTokenProvider.ACCESS_TOKEN_VALID_TIME).toInt())
        val refreshTokenCookie = cookieUtil.createCookie(JwtTokenProvider.REFRESH_TOKEN_NAME, jwtTokenDTO.refreshToken!!, TimeUnit.MILLISECONDS.toSeconds(JwtTokenProvider.REFRESH_TOKEN_VALID_TIME).toInt())

        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        logger.info { "jwtToken $jwtTokenDTO" }
        /*
        try {

        } catch (e: Exception) {
            logger.info { "exchange Exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                null
            )
        }
         */
        return ResponseEntity.ok(jwtTokenDTO)
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestBody refreshTokenDTO: RefreshTokenDTO,
        @AuthenticationPrincipal user: User,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<JwtTokenDTO> {
        logger.info { "refresh token 시도 member Id ${user.username}" }
        var newJwtToken: JwtTokenDTO?

        if(jwtTokenProvider.validateToken(refreshTokenDTO.refreshToken!!)) {
            val member = memberService.findByEmailAddress(refreshTokenDTO.emailAddress!!)
            logger.info { "member loginId ${member.emailAddress} ${member.roles}" }
            logger.info { "request refresh token ${refreshTokenDTO.refreshToken}" }
            logger.info { "member refresh token ${member.refreshToken}" }

            if(member.refreshToken == refreshTokenDTO.refreshToken) {
                newJwtToken = jwtTokenProvider.createJwtTokenDTO(member.emailAddress!!, memberMapper.toDto(member))
                memberService.updateRefreshToken(member.id!!, newJwtToken.refreshToken!!)
                val accessTokenCookie = cookieUtil.createCookie(JwtTokenProvider.ACCESS_TOKEN_NAME, newJwtToken.accessToken!!, TimeUnit.MILLISECONDS.toSeconds(JwtTokenProvider.ACCESS_TOKEN_VALID_TIME).toInt())
                val refreshTokenCookie = cookieUtil.createCookie(JwtTokenProvider.REFRESH_TOKEN_NAME, newJwtToken.refreshToken!!, TimeUnit.MILLISECONDS.toSeconds(JwtTokenProvider.REFRESH_TOKEN_VALID_TIME).toInt())
                response.addCookie(accessTokenCookie)
                response.addCookie(refreshTokenCookie)
                logger.info { "newJwtToken $newJwtToken" }
            } else {
                logger.info { "refresh token is not yours" }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }
        } else {
            logger.info { "refresh token is invalid" }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
        /*
        try {

        } catch (e: Exception) {
            logger.info { "exchange Exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
        }
         */
        return ResponseEntity.ok(newJwtToken)
    }



}
package com.mobilityk.core.config

import com.mobilityk.core.domain.Member
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.common.JwtTokenDTO
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.SecurityLoginService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Base64
import java.util.Date
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
data class JwtTokenProvider(
    private val securityLoginService: SecurityLoginService,
    private val cookieUtil: CookieUtil,
    private val memberService: MemberService
) {
    companion object {
        const val HEADER_AUTH_NAME = "Bearer"
        const val ACCESS_TOKEN_NAME = "access_token"
        const val REFRESH_TOKEN_NAME = "refresh_token"
        //const val ACCESS_TOKEN_VALID_TIME: Long = 1 * 60 * 1000L
        const val ACCESS_TOKEN_VALID_TIME: Long = 3 * 60 * 60 * 1000L
        //const val REFRESH_TOKEN_VALID_TIME: Long = 2 * 60 * 1000L
        const val REFRESH_TOKEN_VALID_TIME: Long = 7 * 24 * 60 * 60 * 1000L
    }
    private var secretKey: String = "greatyun"
    private val HEADER_NAME: String = "Authorization"

    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    fun createJwtTokenDTO(loginId: String, memberDTO: MemberDTO): JwtTokenDTO {
        val now = Date()
        val expireDate = Date(now.time + ACCESS_TOKEN_VALID_TIME)
        val expireDateTime = LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.systemDefault())
        val accessToken = createJwtToken(loginId, memberDTO, expireDate)

        val refreshExpireDate = Date(now.time + REFRESH_TOKEN_VALID_TIME)
        val refreshExpireDateTime = LocalDateTime.ofInstant(refreshExpireDate.toInstant(), ZoneId.systemDefault())
        val refreshToken = createJwtToken(loginId, null, refreshExpireDate)

        return JwtTokenDTO(
            accessToken = accessToken,
            expiredAt = expireDateTime,
            refreshToken = refreshToken,
            refreshExpiredAt = refreshExpireDateTime
        )
    }

    private fun createJwtToken(loginId: String, memberDTO: MemberDTO?, expireDate: Date ): String {
        val claims = Jwts.claims().setSubject(loginId)
        memberDTO?.let { member ->
            if(!member.roles.isNullOrEmpty()) {
                claims["roles"] = member.roles
            }
        }
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(expireDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getAuthentication(accessToken: String): Authentication {
        val userDetails = securityLoginService.loadUserByUsername(getMemberLoginIdFromAccessToken(accessToken))
        return UsernamePasswordAuthenticationToken(userDetails, userDetails?.password, userDetails?.authorities)
    }

    fun getMemberFromAccessToken(accessToken: String): Member {
        return memberService.findByEmailAddress(getMemberLoginIdFromAccessToken(accessToken)!!)
    }

    private fun getMemberLoginIdFromAccessToken(token: String): String? {
        val roles = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body["roles"]
        logger.info { "roles -> $roles" }
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    fun resolveAccessToken(request: HttpServletRequest): String? {
        val headerAuthInfo = request.getHeader(HEADER_NAME)
        var jwtToken: String? = null
        if(!headerAuthInfo.isNullOrEmpty()) {
            // 헤더 검색
            if (headerAuthInfo.startsWith(HEADER_AUTH_NAME)) {
                jwtToken = headerAuthInfo.substring(7, headerAuthInfo.length)
            }
        } else {
            // 쿠키 검색
            val cookie = cookieUtil.getCookie(request, ACCESS_TOKEN_NAME)
            if(cookie != null) {
                jwtToken = cookie.value
            }
        }
        return jwtToken
    }

    fun resolveRefreshToken(request: HttpServletRequest): String? {
        val headerAuthInfo = request.getHeader(HEADER_NAME)
        var jwtToken: String? = null
        if(!headerAuthInfo.isNullOrEmpty()) {
            // 헤더 검색
            if (headerAuthInfo.startsWith(HEADER_AUTH_NAME)) {
                jwtToken = headerAuthInfo.substring(7, headerAuthInfo.length)
            }
        } else {
            // 쿠키 검색
            val cookie = cookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
            if(cookie != null) {
                jwtToken = cookie.value
            }
        }
        return jwtToken
    }

    fun validateToken(jwtToken: String): Boolean {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
        return claims.body.expiration.after(Date())
        /*
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
            claims.body.expiration.after(Date())
        } catch (e: Exception) {
            logger.info { "validation Token exception" }
            logger.info { CommonUtil.getExceptionPrintStack(e) }
            false
        }
         */
    }


}
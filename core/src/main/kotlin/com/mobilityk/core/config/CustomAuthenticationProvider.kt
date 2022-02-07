package com.mobilityk.core.config

import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.SecurityLoginService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
data class CustomAuthenticationProvider(
    private var securityLoginService: SecurityLoginService,
    private var memberService: MemberService,
    private var passwordEncoder: PasswordEncoder
) : AuthenticationProvider{
    override fun authenticate(authentication: Authentication?): Authentication {
        var authToken: UsernamePasswordAuthenticationToken = authentication as UsernamePasswordAuthenticationToken

        val userDetails = securityLoginService.loadUserByUsername(authToken.name)
            ?: throw UsernameNotFoundException("ID 가 존재하지 않습니다.")
        if(!passwordEncoder.matches(authentication.credentials.toString() , userDetails.password)) {
            throw BadCredentialsException("ID/PW 를 확인해주세요")
        }

        return UsernamePasswordAuthenticationToken(userDetails , null , userDetails.authorities)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication?.equals(UsernamePasswordAuthenticationToken::class.java) ?: false
    }

}
package com.mobilityk.core.service

import com.mobilityk.core.config.SecurityUser
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Service
class SecurityLoginService(
    private val memberService: MemberService
): UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String?): UserDetails? {
        var user: User? = null
        try {
            username?.let {
                val member = memberService.findByEmailAddress(it)
                member.updateLastAccessdedAt()

                if(!member.accessYn!!) {
                    throw UsernameNotFoundException("접속 불가 상태")
                }
                user = SecurityUser(member)
                val attributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
                val session = attributes.request.getSession(false)
                session.setAttribute("name" , member.name)
            }
        } catch (e: Exception) {

        }
        return user
    }
}
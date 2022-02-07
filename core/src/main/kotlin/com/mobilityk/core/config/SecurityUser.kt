package com.mobilityk.core.config

import com.mobilityk.core.domain.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class SecurityUser(member: Member) : User(member.id.toString(), member.password, getRoles(member)) {
    companion object {
        private const val ROLE_PREFIX = "ROLE_"
        fun getRoles(member: Member): MutableCollection<out GrantedAuthority> {
            val authorities: MutableList<SimpleGrantedAuthority> = mutableListOf()
            val iterator = member.roles?.iterator()
            iterator?.let {
                while (iterator.hasNext()) {
                    authorities.add(SimpleGrantedAuthority(ROLE_PREFIX + iterator.next().name))
                }
            }

            /*
            when(member.emailAddress) {
                "apiuser" -> {
                    authorities.add(SimpleGrantedAuthority(ROLE_PREFIX + "RESTAPI"))
                }
                "greatyun" -> {
                    authorities.add(SimpleGrantedAuthority(ROLE_PREFIX + "SUPER"))
                }
                else -> {
                    val iterator = member.roles?.iterator()
                    iterator?.let {
                        while (iterator.hasNext()) {
                            authorities.add(SimpleGrantedAuthority(ROLE_PREFIX + iterator.next().name))
                        }
                    }
                }
            }
             */
            return authorities
        }
    }
}




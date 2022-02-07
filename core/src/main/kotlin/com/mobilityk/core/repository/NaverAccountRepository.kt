package com.mobilityk.core.repository

import com.mobilityk.core.domain.NaverAccount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface NaverAccountRepository: JpaRepository<NaverAccount, Long> {

    fun findByNaverId(naverId: String): Optional<NaverAccount>

}
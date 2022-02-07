package com.mobilityk.core.repository

import com.mobilityk.core.domain.EmailAccount
import org.springframework.data.jpa.repository.JpaRepository

interface EmailAccountRepository: JpaRepository<EmailAccount, Long> {

}
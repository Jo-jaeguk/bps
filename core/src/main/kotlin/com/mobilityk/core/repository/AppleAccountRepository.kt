package com.mobilityk.core.repository

import com.mobilityk.core.domain.AppleAccount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AppleAccountRepository: JpaRepository<AppleAccount, Long> {
    fun findByAppleUid(uid: String): Optional<AppleAccount>
}
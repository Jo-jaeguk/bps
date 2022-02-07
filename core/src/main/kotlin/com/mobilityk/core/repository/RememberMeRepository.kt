package com.mobilityk.core.repository

import com.mobilityk.core.domain.RememberMe
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RememberMeRepository: JpaRepository<RememberMe, Long> {
    fun findBySeries(series:String): Optional<RememberMe>
    fun deleteByUsername(username: String): Unit
}
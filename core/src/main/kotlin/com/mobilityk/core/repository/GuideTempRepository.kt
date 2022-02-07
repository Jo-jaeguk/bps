package com.mobilityk.core.repository

import com.mobilityk.core.domain.GuideTemp
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.Optional

interface GuideTempRepository: JpaRepository<GuideTemp, Long> {
    fun findByIdAndMemberId(id: Long, memberId: Long): Optional<GuideTemp>

    fun deleteAllByCreatedAtBefore(createdAt: LocalDateTime): Unit
}
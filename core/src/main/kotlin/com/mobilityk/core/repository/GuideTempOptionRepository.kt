package com.mobilityk.core.repository

import com.mobilityk.core.domain.GuideTempOption
import org.springframework.data.jpa.repository.JpaRepository

interface GuideTempOptionRepository: JpaRepository<GuideTempOption, Long> {
    fun findAllByGuideTempIdAndMemberId(guideTempId: Long, memberId: Long): List<GuideTempOption>
    fun deleteAllByGuideTempIdAndMemberId(guideTempId: Long, memberId: Long)
}
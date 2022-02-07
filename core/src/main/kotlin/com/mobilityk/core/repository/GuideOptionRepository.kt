package com.mobilityk.core.repository

import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuideOption
import org.springframework.data.jpa.repository.JpaRepository

interface GuideOptionRepository: JpaRepository<GuideOption, Long> {
    fun findAllByGuide(guide: Guide): List<GuideOption>
    fun deleteAllByGuide(guide: Guide)
}
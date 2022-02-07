package com.mobilityk.core.repository

import com.mobilityk.core.domain.GuideDamageCheck
import com.mobilityk.core.domain.DamageType
import com.mobilityk.core.domain.Guide
import org.springframework.data.jpa.repository.JpaRepository

interface GuideDamageCheckRepository: JpaRepository<GuideDamageCheck, Long> {
    fun findAllByGuideAndDamageType(guide: Guide, damageType: DamageType): List<GuideDamageCheck>
    fun findAllByGuide(guide: Guide): List<GuideDamageCheck>
    fun deleteAllByGuide(guide: Guide)
}
package com.mobilityk.core.repository

import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuideCheckList
import org.springframework.data.jpa.repository.JpaRepository

interface GuideCheckListRepository: JpaRepository<GuideCheckList, Long> {
    fun findAllByGuide(guide: Guide): List<GuideCheckList>
    fun deleteAllByGuide(guide: Guide)
}
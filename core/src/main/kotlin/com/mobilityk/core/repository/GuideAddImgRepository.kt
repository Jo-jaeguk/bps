package com.mobilityk.core.repository

import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuideAddImg
import com.mobilityk.core.domain.ImgType
import org.springframework.data.jpa.repository.JpaRepository

interface GuideAddImgRepository: JpaRepository<GuideAddImg, Long> {
    fun deleteAllByGuide(guide: Guide)
    fun deleteAllByGuideAndImgType(guide: Guide, imgType: ImgType)
    fun findAllByGuide(guide: Guide): List<GuideAddImg>
    fun findAllByGuideAndImgTypeNotIn(guide: Guide, imgTypes: List<ImgType>): List<GuideAddImg>
    fun findAllByGuideAndImgType(guide: Guide, imgType: ImgType): List<GuideAddImg>
}
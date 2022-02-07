package com.mobilityk.core.repository

import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuidePrice
import com.mobilityk.core.domain.MarketName
import com.mobilityk.core.domain.MarketType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface GuidePriceRepository: JpaRepository<GuidePrice, Long> {

    fun findAllByGuide(guide: Guide): List<GuidePrice>

    fun findAllByGuideAndMarketType(guide: Guide, marketType: MarketType): List<GuidePrice>

    fun deleteAllByGuide(guide: Guide): Unit

    fun findByGuideAndMarketNameAndMarketType(guide: Guide, marketName: String, marketType: MarketType): List<GuidePrice>

    fun findByGuideAndMarketNameCodeAndMarketType(guide: Guide, marketNameCode: MarketName, marketType: MarketType): Optional<GuidePrice>
}
package com.mobilityk.core.dto

import com.mobilityk.core.domain.MarketName
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.PopularType
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated

data class GuidePriceDTO(
    var id: Long? = null,

    var marketName: String? = null,

    var marketType: MarketType? = null,

    var marketNameCode: MarketName? = null,

    var popularType: PopularType? = null,

    var price: Long? = null,

    var retailAvgPrice: Long? = null,

    var bidAvgPrice: Long? = null,

    var guidePrice: Long? = null,

    var sendPrice: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
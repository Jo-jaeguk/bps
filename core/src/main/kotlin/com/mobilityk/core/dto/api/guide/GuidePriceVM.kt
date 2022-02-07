package com.mobilityk.core.dto.api.guide

import com.mobilityk.core.domain.MarketName
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.PopularType
import java.time.LocalDateTime

data class GuidePriceVM(

    var memberId: Long? = null,

    var marketName: String? = null,

    var marketNameCode: MarketName? = null,

    var marketType: MarketType? = null,

    var popularType: PopularType? = null,

    var price: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null

)
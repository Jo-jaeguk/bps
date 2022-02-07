package com.mobilityk.core.dto

import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.domain.PriceConfigType
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class GuidePriceConfigDTO(
    var id: Long? = null,

    @field:NotNull
    var priceConfigType: PriceConfigType? = null,

    var countryType: CountryType? = null,

    var popularType: PopularType? = null,

    var value: String? = null,

    var tablePriceYn: Boolean? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
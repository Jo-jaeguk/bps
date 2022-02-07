package com.mobilityk.core.dto.api.guidePriceConfig

import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.domain.PriceConfigType
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class TableVM(
    @field:NotNull
    var tablePriceList: List<TableDetailVM>? = null
)

data class TableDetailVM(
    @field:NotNull
    var countryType: CountryType? = null,
    @field:NotNull
    var popularType: PopularType? = null,
    @field:NotNull
    var priceConfigType: PriceConfigType? = null,

    @field:NotNull
    var value: Double? = null
)
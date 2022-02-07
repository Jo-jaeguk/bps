package com.mobilityk.core.dto.api.carManufacturer

import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.CountryType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CarManufacturerVM(
    @field:NotEmpty
    @field:NotNull
    var name: String? = null,

    var nameEng: String? = null,
    var published: Boolean? = null,
    @field:NotNull
    var countryType: CountryType? = null,

    @field:NotNull
    var country: Country? = null,
)
package com.mobilityk.core.dto

import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.CountryType
import java.time.LocalDateTime

data class CarModelDTO(

    var id: Long? = null,

    var carManufacturerName: String? = null,

    var carManufacturerCountryType: CountryType? = null,

    var carManufacturerCountry: Country? = null,

    var name: String? = null,

    var nameDetail: String? = null,

    var published: Boolean? = null,

    var carClassList: MutableList<CarClassDTO>? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
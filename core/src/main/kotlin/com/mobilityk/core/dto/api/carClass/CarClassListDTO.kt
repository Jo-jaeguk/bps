package com.mobilityk.core.dto.api.carClass

import com.mobilityk.core.domain.CarManufacturerName
import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.PopularType
import java.time.LocalDateTime

data class CarClassListDTO(
    var id: Long? = null,

    var name: String? = null,

    var popularType: PopularType? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null,

    var carDisplacement: Int? = null,

    var carModelId: Long? = null,

    var carModelName: String? = null,

    var carModelNameDetail: String? = null,

    var carModelPublished: Boolean? = null,

    var carManufacturerId: Long? = null,

    var carManufacturerName: String? = null,

    var carManufacturerPublished: Boolean? = null,

    var carManufacturerCountryType: CountryType? = null,

    var carManufacturerCountry: Country? = null,
)
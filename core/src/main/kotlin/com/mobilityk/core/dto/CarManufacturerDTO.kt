package com.mobilityk.core.dto

import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.CountryType
import java.time.LocalDateTime

data class CarManufacturerDTO(
    var id: Long? = null,

    var name: String? = null,

    var nameKr: String? = null,

    var nameEng: String? = null,

    var imgPath: String? = null,

    var published: Boolean? = null,

    var countryType: CountryType? = null,

    var country: Country? = null,

    var carModelList: MutableList<CarModelDTO>? = mutableListOf(),

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
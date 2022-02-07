package com.mobilityk.core.dto.api.carManufacturer

import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.CountryType

data class CarManufacturerAllVM(
    var deleteList: List<CarManufacturerInner>? = null,
    var addList: List<CarManufacturerInner>? = null,
)

data class CarManufacturerInner(
    var id: Long? = null,
    var name: String? = null,
    var nameKr: String? = null,
    var nameEng: String? = null,
    var countryType: CountryType? = null,
    var country: Country? = null,
)
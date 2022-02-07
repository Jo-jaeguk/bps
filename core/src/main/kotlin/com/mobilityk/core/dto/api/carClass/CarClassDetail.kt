package com.mobilityk.core.dto.api.carClass

import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.dto.CarManufacturerDTO
import com.mobilityk.core.dto.CarModelDTO
import java.time.LocalDateTime

data class CarClassDetail(

    var id: Long? = null,

    var name: String? = null,

    var carManufacturer: CarManufacturerDTO? = null,

    var carModel: CarModelDTO? = null,

    var popularType: PopularType? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
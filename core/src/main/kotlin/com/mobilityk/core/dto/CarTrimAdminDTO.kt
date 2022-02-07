package com.mobilityk.core.dto

import com.mobilityk.core.domain.PopularType
import java.time.LocalDateTime

data class CarTrimAdminDTO(

    var id: Long? = null,

    var name: String? = null,

    var carManufacturerId: Long? = null,

    var carManufacturer: String? = null,

    var carModel: String? = null,

    var carModelDetail: String? = null,

    var carClass: String? = null,

    var carDisplacement: Int? = null,

    var popularType: PopularType? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
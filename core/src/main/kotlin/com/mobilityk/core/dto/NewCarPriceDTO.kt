package com.mobilityk.core.dto

import java.time.LocalDateTime

data class NewCarPriceDTO(
    var id: Long? = null,

    var carManufacturerName: String? = null,

    var carModelName: String? = null,

    var carModelDetailName: String? = null,

    var carTrimName: String? = null,

    var price: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
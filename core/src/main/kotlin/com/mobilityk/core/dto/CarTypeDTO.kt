package com.mobilityk.core.dto

import com.mobilityk.core.domain.CarTypeEnum
import java.time.LocalDateTime

data class CarTypeDTO(

    var id: Long? = null,

    var carType: CarTypeEnum? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
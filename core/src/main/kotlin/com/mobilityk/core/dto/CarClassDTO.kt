package com.mobilityk.core.dto

import com.mobilityk.core.domain.PopularType
import java.time.LocalDateTime

data class CarClassDTO(

    var id: Long? = null,

    var name: String? = null,

    var carDisplacement: Int? = null,

    var published: Boolean? = null,

    var popularType: PopularType? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
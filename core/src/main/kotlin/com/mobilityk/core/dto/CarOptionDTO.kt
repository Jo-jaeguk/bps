package com.mobilityk.core.dto

import com.mobilityk.core.domain.EnumYn
import java.time.LocalDateTime

data class CarOptionDTO(

    var id: Long? = null,

    var option: String? = null,

    var published: Boolean? = null,

    var choosedYn: EnumYn? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
package com.mobilityk.core.dto

import java.time.LocalDateTime

data class CarColorDTO(
    var id: Long? = null,

    var color: String? = null,

    var published: Boolean? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
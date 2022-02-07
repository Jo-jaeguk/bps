package com.mobilityk.core.dto

import java.time.LocalDateTime

data class CarTrimDTO(

    var id: Long? = null,

    var name: String? = null,

    var published: Boolean? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
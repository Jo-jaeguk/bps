package com.mobilityk.core.dto

import java.time.LocalDateTime

data class NewCarDealerLocationDTO(
    var id: Long? = null,

    var name: String? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
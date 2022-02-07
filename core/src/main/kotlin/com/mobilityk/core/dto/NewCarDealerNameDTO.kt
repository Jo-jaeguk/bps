package com.mobilityk.core.dto

import java.time.LocalDateTime

data class NewCarDealerNameDTO(
    var id: Long? = null,

    var name: String? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
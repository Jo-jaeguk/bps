package com.mobilityk.core.dto

import java.time.LocalDateTime

data class EmailAccountDTO(
    var id: Long? = null,

    var memberId: Long? = null,

    var email: String? = null,

    var password: String? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
package com.mobilityk.core.dto

import java.time.LocalDateTime

data class GuideOptionDTO(

    var id: Long? = null,

    var option: String? = null,

    var writerId: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
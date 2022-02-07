package com.mobilityk.core.dto

import java.time.LocalDateTime

data class GuideCheckListDTO(

    var id: Long? = null,

    var checkListConfigId: Long? = null,

    var question: String? = null,

    var price: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
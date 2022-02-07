package com.mobilityk.core.dto

import com.mobilityk.core.domain.EnumYn
import java.time.LocalDateTime

data class CheckListConfigDTO(

    var id: Long? = null,

    var question: String? = null,

    var price: Long? = null,

    var published: Boolean? = null,

    var orderIndex: Int? = null,

    var writerLoginId: String? = null,

    var choosedYn: EnumYn? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
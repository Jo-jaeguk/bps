package com.mobilityk.core.dto.buyandsell

import com.mobilityk.core.domain.EnumYn
import java.time.LocalDateTime

data class SellDTO(
    var id: Long? = null,

    var incomeYn: EnumYn? = null,

    var needDocumentYn: EnumYn? = null,

    var sellTarget: String? = null,

    var hopePrice: Long? = null,

    var sellPrice: Long? = null,

    var otherCommission: Long? = null,

    var note: String? = null,

    var orderIndex: Int? = null,

    var useYn: EnumYn? = null,

    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {

}
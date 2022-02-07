package com.mobilityk.core.dto.buyandsell

import com.mobilityk.core.domain.buyandsell.ItemType
import java.time.LocalDateTime

data class BuyDTO(
    var id: Long? = null,

    var itemType: ItemType? = null,

    var name: String? = null,

    var price: Long? = null,

    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {

}
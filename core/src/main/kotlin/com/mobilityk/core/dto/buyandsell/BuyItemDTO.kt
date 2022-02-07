package com.mobilityk.core.dto.buyandsell

import com.mobilityk.core.domain.buyandsell.ItemType
import java.time.LocalDateTime

data class BuyItemDTO(
    var id: Long? = null,

    var name: String? = null,

    var itemType: ItemType? = null,

    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {

}
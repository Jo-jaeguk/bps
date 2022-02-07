package com.mobilityk.core.dto.buyandsell

import java.time.LocalDateTime

data class SellerDTO(
    var id: Long? = null,

    var name: String? = null,


    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {

}
package com.mobilityk.core.dto.base

import java.time.LocalDateTime

open class BaseDTO(
    var pkid: Long? = null,

    var result: Int= 0,

    var description: String? = null,

    var start: Int = 0,

    var length: Int = 0,

    var draw: Int = 0,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
) {

}
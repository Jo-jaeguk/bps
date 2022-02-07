package com.mobilityk.core.dto

import com.mobilityk.core.domain.ConfigType
import java.time.LocalDateTime

data class ConfigDTO(
    var id: Long? = null,

    var configType: ConfigType? = null,

    var value: String? = null,

    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {
}
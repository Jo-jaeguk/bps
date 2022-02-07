package com.mobilityk.core.dto

import com.mobilityk.core.domain.MappingType
import java.time.LocalDateTime

data class UploadMappingDTO(
    var id: Long? = null,

    var uploadId: Long? = null,

    var mappingId: Long? = null,

    var mappingType: MappingType? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
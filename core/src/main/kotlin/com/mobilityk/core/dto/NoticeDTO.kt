package com.mobilityk.core.dto

import java.time.LocalDateTime

data class NoticeDTO(
    var id: Long? = null,

    var title: String? = null,

    var body: String? = null,

    var writerId: Long? = null,

    var attatchMents: List<UploadMappingDTO>? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
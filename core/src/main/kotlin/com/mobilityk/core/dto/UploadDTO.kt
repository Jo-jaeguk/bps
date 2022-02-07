package com.mobilityk.core.dto

import java.time.LocalDateTime

data class UploadDTO(
    var id: Long? = null,

    var url: String? = null,

    var imgUrl: String? = null,

    var path: String? = null,

    var fileName: String? = null,

    var writerId: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
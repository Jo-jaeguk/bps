package com.mobilityk.core.dto

import java.time.LocalDateTime

data class GuidePriceConfigCommentDTO(
    var id: Long? = null,

    var body: String? = null,

    var writerId: Long? = null,

    var member: MemberDTO? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
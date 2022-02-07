package com.mobilityk.core.dto

import java.time.LocalDateTime

data class MemberCommentDTO(
    var id: Long? = null,

    //var member: MemberDTO? = null,

    var body: String? = null,

    var writerId: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
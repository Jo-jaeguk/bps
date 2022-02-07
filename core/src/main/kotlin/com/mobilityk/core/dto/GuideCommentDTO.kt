package com.mobilityk.core.dto

import com.mobilityk.core.domain.CommentType
import java.time.LocalDateTime

data class GuideCommentDTO(
    var id: Long? = null,

    var commentType: CommentType? = null,

    var body: String? = null,

    var writerId: Long? = null,

    var member: MemberDTO? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
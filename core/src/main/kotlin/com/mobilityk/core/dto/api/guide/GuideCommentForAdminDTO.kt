package com.mobilityk.core.dto.api.guide

import com.mobilityk.core.domain.CommentType
import com.mobilityk.core.dto.MemberDTO
import java.time.LocalDateTime

data class GuideCommentForAdminDTO(
    var id: Long? = null,

    var commentType: CommentType? = null,

    var member: MemberDTO? = null,

    var body: String? = null,

    var writerId: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
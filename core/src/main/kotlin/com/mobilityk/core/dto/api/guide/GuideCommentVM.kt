package com.mobilityk.core.dto.api.guide

import com.mobilityk.core.domain.CommentType

data class GuideCommentVM(
    var commentType: CommentType? = null,
    var body: String? = null,
)
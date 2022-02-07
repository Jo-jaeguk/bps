package com.mobilityk.core.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mobilityk.core.domain.AccountType
import com.mobilityk.core.domain.Gender
import com.mobilityk.core.domain.MemberComment
import com.mobilityk.core.enumuration.ROLE
import java.time.LocalDateTime

data class MemberDTO(
    var id: Long? = null,

    var emailAddress: String? = null,

    var accountType: AccountType? = null,

    @JsonIgnore
    var password: String? = null,

    var gender: Gender? = null,

    var name: String? = null,

    var birthDate: LocalDateTime? = null,

    var phone: String? = null,

    var position: String? = null,

    var activated: Boolean? = null,

    var groupName: String? = null,

    var branch: String? = null,

    var manageBranch: String? = null,

    var point: Long? = null,

    var accessYn: Boolean? = null,

    var kakaoTalkYn: Boolean? = null,

    var comment: String? = null,

    var accessToken: String? = null,

    var refreshToken: String? = null,

    var guideCount: Long? = null,

    var roles: MutableSet<ROLE>? = null,

    var commentList: MutableList<MemberCommentDTO>? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null,

    var lastAccessedAt: LocalDateTime? = null
)
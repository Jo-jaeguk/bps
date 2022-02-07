package com.mobilityk.core.dto.api.member

import com.mobilityk.core.domain.Gender
import com.mobilityk.core.enumuration.ROLE
import java.time.LocalDateTime

data class MemberVM(
    var id: Long? = null,

    var emailAddress: String? = null,

    var password: String? = null,

    var name: String? = null,

    var gender: Gender? = null,

    var birthDate: LocalDateTime? = null,

    var phone: String? = null,

    var position: String? = null,

    var groupName: String? = null,

    var branch: String? = null,

    var manageBranch: String? = null,

    var point: Long? = null,

    var accessYn: Boolean? = null,

    var kakaoTalkYn: Boolean? = null,

    var comment: String? = null,

    var accessToken: String? = null,

    var refreshToken: String? = null,

    var roles: MutableSet<ROLE>? = mutableSetOf()
)
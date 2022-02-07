package com.mobilityk.core.repository.search

import com.mobilityk.core.enumuration.ROLE
import java.time.LocalDateTime

data class MemberSearchOption(
    var search: String? = null,
    var email: String? = null,
    var createdAt: LocalDateTime? = null,
    var accessYn: Boolean? = null,
    var activated: Boolean? = null,
    var roles: MutableSet<ROLE>? = null,
    var notInRole: ROLE? = null
)
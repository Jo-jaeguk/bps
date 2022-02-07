package com.mobilityk.core.dto.api.member

import com.mobilityk.core.domain.AccountType
import com.mobilityk.core.domain.Gender
import com.mobilityk.core.enumuration.ROLE
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

data class MemberJoinVM(
    @field:NotEmpty
    @field:Email
    var emailAddress: String? = null,

    @field:NotEmpty
    var password: String? = null,

    @field:NotNull
    var accountType: AccountType? = null,

    var roles: MutableSet<ROLE>? = null,

    var gender: Gender? = null,

)
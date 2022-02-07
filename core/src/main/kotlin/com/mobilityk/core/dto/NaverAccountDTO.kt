package com.mobilityk.core.dto

import java.time.LocalDateTime

data class NaverAccountDTO(
    var id: Long? = null,

    var naverId: String? = null,

    var memberId: Long? = null,

    var nickname: String? = null,

    var name: String? = null,

    var email: String? = null,

    var gender: String? = null,

    var age: String? = null,

    var birthday: String? = null,

    var profileImage: String? = null,

    var birthyear: String? = null,

    var mobile: String? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
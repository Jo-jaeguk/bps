package com.mobilityk.core.dto

import java.time.LocalDateTime

data class KakaoAccountDTO(
    var id: Long? = null,

    var kakaoId: Long? = null,

    var memberId: Long? = null,

    var email: String? = null,

    var ageRange: String? = null,

    var birthYear: String? = null,

    var birthDay: String? = null,

    var gender: String? = null,

    var phoneNumber: String? = null,

    var nickName: String? = null,

    var thumbnailImageUrl: String? = null,

    var profileImageUrl: String? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
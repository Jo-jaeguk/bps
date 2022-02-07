package com.mobilityk.core.dto.api.naver

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverUserInfo(
    var resultcode: String? = null,
    var message: String? = null,
    var response: UserResponse? = null
)

data class UserResponse(
    var id: String? = null,
    var nickname: String? = null,
    var name: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var age: String? = null,
    var birthday: String? = null,
    @JsonProperty("profile_image")
    var profileImage: String? = null,
    var birthyear: String? = null,
    var mobile: String? = null
)
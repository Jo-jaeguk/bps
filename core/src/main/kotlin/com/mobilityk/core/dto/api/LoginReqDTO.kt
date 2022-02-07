package com.mobilityk.core.dto.api

data class LoginReqDTO(
    var emailAddress: String? = null,
    var password: String? = null
)
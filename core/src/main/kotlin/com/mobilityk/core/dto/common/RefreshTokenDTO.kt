package com.build.core.dto.common

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshTokenDTO(
    @JsonProperty("refresh_token")
    val refreshToken: String? = null,
    val emailAddress: String? = null
)
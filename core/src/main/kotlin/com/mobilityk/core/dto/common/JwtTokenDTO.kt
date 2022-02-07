package com.mobilityk.core.dto.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.time.LocalDateTime

data class JwtTokenDTO(
    @JsonProperty("access_token")
    val accessToken: String? = null,
    @JsonProperty("refresh_token")
    val refreshToken: String? = null,
    @JsonProperty("expired_at")
    val expiredAt: LocalDateTime? = null,
    @JsonProperty("refresh_expired_at")
    val refreshExpiredAt: LocalDateTime? = null,
): Serializable
package com.mobilityk.core.dto.api.kakao

data class KakaoResponse(
    var token_type: String? = null,
    var access_token: String? = null,
    var expires_in: String? = null,
    var refresh_token: String? = null,
    var refresh_token_expires_in: String? = null,
    var scope: String? = null,
)
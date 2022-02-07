package com.mobilityk.core.dto.api.kakao

import java.time.Instant

data class KakaoUserInfo(
    var id: Long? = null,
    var has_signed_up: Boolean? = null,
    var kakao_account: KakaoAccount? = null
)

data class KakaoAccount(
    var profile_needs_agreement: Boolean? = null,
    var profile: Profile? = null,
    var email_needs_agreement: Boolean? = null,
    var is_email_valid: Boolean? = null,
    var is_email_verified: Boolean? = null,
    var email: String? = null,
    var age_range_needs_agreement: Boolean? = null,
    var age_range: String? = null,
    var birthyear_needs_agreement: Boolean? = null,
    var birthyear: String? = null,
    var birthday_needs_agreement: Boolean? = null,
    var birthday: String? = null,
    var birthday_type: String? = null,
    var gender_needs_agreement: Boolean? = null,
    var gender: String? = null,
    var phone_number_needs_agreement: Boolean? = null,
    var phone_number: String? = null,
    var ci_needs_agreement: Boolean? = null,
    var ci: String? = null,
    var ci_authenticated_at: Instant? = null
)

data class Profile(
    var nickname: String? = null,
    var thumbnail_image_url: String? = null,
    var profile_image_url: String? = null,
    var is_default_image: Boolean? = null
)
package com.mobilityk.core.dto.api.nice

data class NiceUserInfo(
    var name: String? = null,
    var phone: String? = null,
    var phoneCompany: String? = null,
    var birthDate: String? = null,
    var gender: String? = null,
    var message: String? = null,
    var result: Int? = null
)
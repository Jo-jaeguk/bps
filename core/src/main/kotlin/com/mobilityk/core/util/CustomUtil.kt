package com.mobilityk.core.util

import org.springframework.stereotype.Component

@Component
class CustomUtil {

    fun phoneFormat(phone: String): String {
        return CommonUtil.phoneFormat(phone)
    }
}
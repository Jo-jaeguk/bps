package com.mobilityk.core.dto

import com.mobilityk.core.domain.ImgType

data class GuideAddImgDTO(
    var id: Long? = null,
    var imgUrl: String? = null,
    var imgType: ImgType? = null,
) {
}
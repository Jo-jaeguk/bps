package com.mobilityk.core.dto.api.carClass

import com.mobilityk.core.domain.PopularType

data class CarClassVM(
    var carModelId: Long? = null,
    var carClassName: String? = null,
    var carClassDisplacement: Int? = null,
    var popularType: PopularType? = null
)
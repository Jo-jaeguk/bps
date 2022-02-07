package com.mobilityk.core.dto.api.config

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.DamageLocation

data class DamageConfigRq(
    var carType: CarTypeEnum? = null,
    var priceList: List<Price>? = null,
    var smallBase: Double? = null,
    var bigBase: Double? = null,
    var foreignBase: Double? = null,
    var midBase: Double? = null,
    var rvBase: Double? = null,

    var smallBasePrice: Long? = null,
    var bigBasePrice: Long? = null,
    var foreignBasePrice: Long? = null,
    var midBasePrice: Long? = null,
    var rvBasePrice: Long? = null,
) {
}

data class Price(
    var damageLocation: DamageLocation? = null,
    var giGyoHwanPrice: Long? = null,
    var giPangumPrice: Long? = null,
    var giDosaekPrice: Long? = null,
    var needGyoHwanPrice: Long? = null,
    var needPangumPrice: Long? = null,
    var needDosaekPrice: Long? = null,
    var otherPrice: Long? = null,
)
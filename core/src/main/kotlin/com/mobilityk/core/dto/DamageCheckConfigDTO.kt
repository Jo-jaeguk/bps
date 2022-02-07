package com.mobilityk.core.dto

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.DamageLocation
import java.time.LocalDateTime

data class DamageCheckConfigDTO(

    var id: Long? = null,

    var number: Int? = null,

    var carType: CarTypeEnum? = null,

    var carTypeStr: String? = null,

    var damageLocation: DamageLocation? = null,

    var damageLocationStr: String? = null,

    var giGyoHwanPrice: Long? = null,

    var giPangumPrice: Long? = null,

    var giDosaekPrice: Long? = null,

    var needGyoHwanPrice: Long? = null,

    var needPangumPrice: Long? = null,

    var needDosaekPrice: Long? = null,

    var otherPrice: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
) {
    fun convertToStr() {
        this.carTypeStr = carType?.description
        this.damageLocationStr = damageLocation?.description
    }
}
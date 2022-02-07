package com.mobilityk.core.dto

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.DamageLocation
import com.mobilityk.core.domain.DamageType
import java.time.LocalDateTime
import javax.persistence.Column

data class GuideDamageCheckDTO(

    var id: Long? = null,

    var damageType: DamageType? = null,

    var damageTypeStr: String? = null,

    var carType: CarTypeEnum? = null,

    var carTypeStr: String? = null,

    var price: Long? = null,

    var damageLocation: DamageLocation? = null,

    var damageLocationStr: String? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
) {
    fun convertToStr() {
        this.carTypeStr = carType?.description
        this.damageLocationStr = damageLocation?.description
        this.damageTypeStr = damageType?.description
    }
}


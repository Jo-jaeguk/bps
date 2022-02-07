package com.mobilityk.core.dto.api.guide

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.DamageLocation
import com.mobilityk.core.domain.DamageType
import com.mobilityk.core.domain.TireWearType
import com.mobilityk.core.dto.GuideCheckListDTO

data class GuideInspectVM(
    var guideId: Long? = null,
    var checkListIds: List<Long>? = null,
    var damageList: MutableList<DamageInfo>? = null,
    var carType: CarTypeEnum? = null,
    var tireWearType: TireWearType? = null,
    var tireWearPrice: Long? = null,
    var tireWearReason: String? = null,
    var tireWearCount: Int? = null,



    var checkList: MutableList<GuideCheckListDTO>? = null,
) {
}

data class DamageInfo (
    var carType: CarTypeEnum? = null,
    var damageType: DamageType? = null,
    var damageLocationEnum: DamageLocation? = null,
    var price: Long? = null
)

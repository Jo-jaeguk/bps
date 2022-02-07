package com.mobilityk.core.dto.api.guide

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.TireWearType
import javax.persistence.Column
import javax.persistence.Lob

data class GuideInspectSaveVM(
    var guideId: Long? = null,
    var mileage: Int? = null,

    var carNumber: String? = null,

    var carOptionIdList: List<Long>? = null,

    var carType: CarTypeEnum? = null,
    var checkListIds: List<Long>? = null,

    var tireWearType: TireWearType? = null,      // 타이어 마모 개수
    var tireWearPrice: Long? = null,
    var tireWearReason: String? = null,

    var checkListMinusPrice: Long? = null,
    var damageList: List<DamageInfo>? = null,
    var minus: GuideInspectVM? = null,

    var evaluatorMinusPrice: Long? = null,
    var evaluatorMinusReason: String? = null,

) {
}
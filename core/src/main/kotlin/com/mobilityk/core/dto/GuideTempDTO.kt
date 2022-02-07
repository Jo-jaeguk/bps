package com.mobilityk.core.dto

import com.mobilityk.core.domain.Accident
import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.FuelType
import com.mobilityk.core.domain.MotorType

data class GuideTempDTO(
    var id: Long? = null,

    var memberId: Long? = null,

    var country: Country? = null,

    var carLocation: String? = null,

    var carColor: String? = null,

    var carManufacturer: String? = null,

    var carManufacturerId: Long? = null,

    var carNumber: String? = null,

    var carModel: String? = null,

    var carModelDetail: String? = null,

    var carModelId: Long? = null,

    var carClassName: String? = null,

    var carClassId: Long? = null,

    var carTrim: String? = null,

    var carTrimId: Long? = null,

    var carDisplacement: Int? = null,

    var carMadedYear: Int? = null,

    var carMadedMonth: Int? = null,

    var accident: Accident? = null,

    var fuelType: FuelType? = null,

    var motorType: MotorType? = null,

    var mileage: Int? = null,

    var option1: String? = null,

    var option2: String? = null,

    var option3: String? = null,

    var customerName: String? = null,

    var customerContact: String? = null,
) {
}
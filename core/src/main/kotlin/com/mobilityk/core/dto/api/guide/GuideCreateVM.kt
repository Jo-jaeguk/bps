package com.mobilityk.core.dto.api.guide

import com.mobilityk.core.domain.Accident
import com.mobilityk.core.domain.Country
import com.mobilityk.core.domain.FuelType
import com.mobilityk.core.domain.MotorType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class GuideCreateVM(

    var guideTempId: Long? = null,

    var memberId: Long? = null,

    var country: Country? = null,

    var carLocation: String? = null,

    var carManufacturer: String? = null,

    var carManufacturerId: Long? = null,

    var carNumber: String? = null,

    var carModel: String? = null,

    var carModelId: Long? = null,

    var carClassId: Long? = null,

    var carColorId: Long? = null,

    var carClassName: String? = null,

    var carTrimId: Long? = null,

    var carTrim: String? = null,

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

)
package com.mobilityk.core.dto.api.carTrim

import javax.validation.constraints.NotNull

data class CarTrimVM(
    @field:NotNull
    var carClassId: Long? = null,
    @field:NotNull
    var carTrimName: String? = null
)
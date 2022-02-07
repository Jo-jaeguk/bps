package com.mobilityk.core.dto.api.guidePriceConfig

import javax.validation.constraints.NotEmpty

data class EverageVM(
    @field:NotEmpty
    var domesticEverageValue: String? = null,
    @field:NotEmpty
    var internationalEverageValue: String? = null
)
package com.mobilityk.core.dto.api.nice

import com.fasterxml.jackson.annotation.JsonProperty

data class NiceAuthDTO(
    @JsonProperty("EncodeData")
    var encodeData: String? = null
)
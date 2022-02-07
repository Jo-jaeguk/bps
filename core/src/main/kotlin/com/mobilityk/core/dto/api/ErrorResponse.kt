package com.mobilityk.core.dto.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ErrorResponse(

    @field:JsonProperty("result_code")
    var resultCode: String? = null,

    @field:JsonProperty("http_status")
    var httpStatus: Int? = null,

    @field:JsonProperty("http_method")
    var httpMethod: String? = null,

    var message: String? = null,

    var path: String? = null,

    var timestamp: LocalDateTime? = null,

    @field:JsonProperty("error_details")
    var errorDetails: MutableList<ErrorDetail>? = mutableListOf()
)

data class ErrorDetail(
    var field: String? = null,
    var message: String? = null,
    @field:JsonProperty("requested_value")
    var requestedValue: Any? = null
)
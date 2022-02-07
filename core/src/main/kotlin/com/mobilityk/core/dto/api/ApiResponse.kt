package com.mobilityk.core.dto.api

import java.io.Serializable

data class ApiResponse<T> (
    var data: T? = null
    , var isArray: Boolean? = null
    , var error: ErrorResponse? = null
    //, var contents: List<T>? = null
    , var pagination : Pagination? = null
    , var result: Int = 0
    , var message: String? = ""
) : Serializable  {
    init {
        this.isArray = false
        this.result = 0
        this.message = "완료되었습니다."
    }
}

data class Pagination(
    var totalElements: Long = 0,
    var totalPages: Int = 0,
    var startPage:Int = 0,
    var endPage:Int = 0,
    var rangeSize:Int = 10,
    var curRange:Int = 0,
    var page:Int = 0,
    var size: Int = 0,
    var prevPage: Int= 0,
    var nextPage: Int= 0,
    var isFirst: Boolean? = null,
    var isLast: Boolean? = null,
    var sort: String? = null,
    var direction: String? = null,
)
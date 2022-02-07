package com.mobilityk.core.util

import java.time.Instant

open class DataTableDTO {
    var pkid: Long? = null
    var updDate: Instant? = Instant.now()
    var regDate: Instant? = Instant.now()
    var search: String? = null
    var searchValue: String? = null
    var searchType: String? = null
    var draw: Int = 0
    var start: Int = 0
    var length: Int = 0
    var filter: String? = null
    var orderColumn: String? = null
    var orderDir: String? = null
    var recordsTotal: String? = null
    var recordsFiltered: String? = null
    var result: Int = 0
    var responseCode: Int = 0
    var msg: String? = ""
    var data: MutableList<*> = mutableListOf<Any>()
}
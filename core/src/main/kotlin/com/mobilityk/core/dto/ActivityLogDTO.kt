package com.mobilityk.core.dto

import com.mobilityk.core.domain.LogType
import java.time.LocalDateTime

data class ActivityLogDTO(
    var id: Long? = null,

    var writerId: Long? = null,

    var writerName: String? = null,

    var writerPhone: String? = null,

    var memberId: Long? = null,

    var memberName: String? = null,

    var memberEmail: String? = null,

    var memberPhone: String? = null,

    var memberPosition: String? = null,

    var memberGroup: String? = null,

    var memberBranch: String? = null,

    var body: String? = null,

    var logType: LogType? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
) {
    fun replaceNewLine() {
        this.body?.let {
            //this.body = it.replace("\n" , "<br>")
        }
    }
}
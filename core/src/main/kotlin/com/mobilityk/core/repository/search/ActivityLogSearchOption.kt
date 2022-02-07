package com.mobilityk.core.repository.search

import com.mobilityk.core.domain.LogType
import java.time.LocalDateTime

data class ActivityLogSearchOption(
    var search: String? = null,
    var startedAt: LocalDateTime? = null,
    var endedAt: LocalDateTime? = null,
    var logType: LogType? = null,
)
package com.mobilityk.core.dto

import com.mobilityk.core.domain.NotificationType
import com.mobilityk.core.domain.TargetType
import java.time.LocalDateTime

data class NotificationDTO(
    var id: Long? = null,

    var title: String? = null,

    var body: String? = null,

    var readYn: Boolean? = null,

    var notificationType: NotificationType? = null,

    var targetType: TargetType? = null,

    var memberId: Long? = null,

    var writerId: Long? = null,

    var updatedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null
)
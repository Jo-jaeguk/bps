package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.Notification
import com.mobilityk.core.dto.NotificationDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NotificationMapper : EntityMapper<NotificationDTO, Notification> {
}

package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.GuideNotification
import com.mobilityk.core.dto.GuideNotificationDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuideNotificationMapper : EntityMapper<GuideNotificationDTO, GuideNotification> {
}

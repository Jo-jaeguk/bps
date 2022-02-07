package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.ActivityLog
import com.mobilityk.core.dto.ActivityLogDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface ActivityLogMapper : EntityMapper<ActivityLogDTO, ActivityLog> {
}

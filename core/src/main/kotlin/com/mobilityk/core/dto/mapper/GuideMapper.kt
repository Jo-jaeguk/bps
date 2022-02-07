package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.Guide
import com.mobilityk.core.dto.GuideDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuideMapper : EntityMapper<GuideDTO, Guide> {
}

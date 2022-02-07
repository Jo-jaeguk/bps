package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.GuideTemp
import com.mobilityk.core.dto.GuideTempDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuideTempMapper : EntityMapper<GuideTempDTO, GuideTemp> {
}

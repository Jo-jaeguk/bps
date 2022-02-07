package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.GuideDamageCheck
import com.mobilityk.core.dto.GuideDamageCheckDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuideDamageCheckMapper : EntityMapper<GuideDamageCheckDTO, GuideDamageCheck> {
}

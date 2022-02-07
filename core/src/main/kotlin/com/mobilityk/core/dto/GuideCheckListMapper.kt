package com.mobilityk.core.dto

import com.mobilityk.core.domain.GuideCheckList
import com.mobilityk.core.dto.mapper.EntityMapper
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuideCheckListMapper : EntityMapper<GuideCheckListDTO, GuideCheckList> {
}

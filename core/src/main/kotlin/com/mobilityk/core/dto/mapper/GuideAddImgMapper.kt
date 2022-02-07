package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.GuideAddImg
import com.mobilityk.core.dto.GuideAddImgDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuideAddImgMapper : EntityMapper<GuideAddImgDTO, GuideAddImg> {
}


package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.GuidePriceConfig
import com.mobilityk.core.domain.GuidePriceConfigComment
import com.mobilityk.core.dto.GuidePriceConfigCommentDTO
import com.mobilityk.core.dto.GuidePriceConfigDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuidePriceConfigCommentMapper : EntityMapper<GuidePriceConfigCommentDTO, GuidePriceConfigComment> {


}


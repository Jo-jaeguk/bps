package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.GuideComment
import com.mobilityk.core.dto.GuideCommentDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuideCommentMapper : EntityMapper<GuideCommentDTO, GuideComment> {
}


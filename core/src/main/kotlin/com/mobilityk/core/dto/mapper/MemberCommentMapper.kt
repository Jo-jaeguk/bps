package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.MemberComment
import com.mobilityk.core.dto.MemberCommentDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface MemberCommentMapper : EntityMapper<MemberCommentDTO, MemberComment> {
}
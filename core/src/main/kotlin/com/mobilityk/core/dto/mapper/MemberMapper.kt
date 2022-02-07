package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.Member
import com.mobilityk.core.dto.MemberDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface MemberMapper : EntityMapper<MemberDTO, Member> {
}
package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.Branch
import com.mobilityk.core.dto.BranchDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface BranchMapper : EntityMapper<BranchDTO, Branch> {
}

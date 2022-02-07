package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.UploadMapping
import com.mobilityk.core.dto.UploadMappingDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UploadMappingMapper : EntityMapper<UploadMappingDTO, UploadMapping> {
}

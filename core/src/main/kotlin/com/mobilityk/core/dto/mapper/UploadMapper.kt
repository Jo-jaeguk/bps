package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.Upload
import com.mobilityk.core.dto.UploadDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UploadMapper : EntityMapper<UploadDTO, Upload> {
}

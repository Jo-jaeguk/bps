package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.Config
import com.mobilityk.core.dto.ConfigDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface ConfigMapper : EntityMapper<ConfigDTO, Config> {
}

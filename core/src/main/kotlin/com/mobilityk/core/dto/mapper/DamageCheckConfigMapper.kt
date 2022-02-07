package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.DamageCheckConfig
import com.mobilityk.core.dto.DamageCheckConfigDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface DamageCheckConfigMapper : EntityMapper<DamageCheckConfigDTO, DamageCheckConfig> {
}

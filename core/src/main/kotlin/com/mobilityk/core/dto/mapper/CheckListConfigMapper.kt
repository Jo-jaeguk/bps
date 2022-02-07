package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.CheckListConfig
import com.mobilityk.core.dto.CheckListConfigDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CheckListConfigMapper : EntityMapper<CheckListConfigDTO, CheckListConfig> {
}

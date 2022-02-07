package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.GuidePrice
import com.mobilityk.core.domain.GuidePriceConfig
import com.mobilityk.core.dto.GuidePriceConfigDTO
import com.mobilityk.core.dto.GuidePriceDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GuidePriceConfigMapper : EntityMapper<GuidePriceConfigDTO, GuidePriceConfig> {

}


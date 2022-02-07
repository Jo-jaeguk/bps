package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.BuyType
import com.mobilityk.core.dto.BuyTypeDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface BuyTypeMapper : EntityMapper<BuyTypeDTO, BuyType> {
}

package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.Sell
import com.mobilityk.core.dto.buyandsell.SellDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SellMapper : EntityMapper<SellDTO, Sell> {
}

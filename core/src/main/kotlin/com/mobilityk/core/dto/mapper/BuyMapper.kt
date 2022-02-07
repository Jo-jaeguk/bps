package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.Buy
import com.mobilityk.core.dto.buyandsell.BuyDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface BuyMapper : EntityMapper<BuyDTO, Buy> {
}

package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.BuyAndSell
import com.mobilityk.core.dto.buyandsell.BuyAndSellDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface BuyAndSellMapper : EntityMapper<BuyAndSellDTO, BuyAndSell> {
}

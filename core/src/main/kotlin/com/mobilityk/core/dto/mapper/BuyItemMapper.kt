package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.BuyItem
import com.mobilityk.core.dto.buyandsell.BuyItemDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface BuyItemMapper : EntityMapper<BuyItemDTO, BuyItem> {
}

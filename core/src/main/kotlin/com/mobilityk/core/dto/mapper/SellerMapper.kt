package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.Seller
import com.mobilityk.core.dto.buyandsell.SellerDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SellerMapper : EntityMapper<SellerDTO, Seller> {
}

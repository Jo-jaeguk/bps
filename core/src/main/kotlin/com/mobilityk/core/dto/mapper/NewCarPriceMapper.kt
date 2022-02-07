package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.NewCarPrice
import com.mobilityk.core.dto.NewCarPriceDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NewCarPriceMapper : EntityMapper<NewCarPriceDTO, NewCarPrice> {
}

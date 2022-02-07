package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.BuyAndSell
import com.mobilityk.core.domain.buyandsell.CarLocation
import com.mobilityk.core.dto.buyandsell.BuyAndSellDTO
import com.mobilityk.core.dto.buyandsell.CarLocationDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CarLocationMapper : EntityMapper<CarLocationDTO, CarLocation> {
}

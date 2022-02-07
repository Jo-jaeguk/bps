package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.CarManufacturer
import com.mobilityk.core.dto.CarManufacturerDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CarManufacturerMapper : EntityMapper<CarManufacturerDTO, CarManufacturer> {
}

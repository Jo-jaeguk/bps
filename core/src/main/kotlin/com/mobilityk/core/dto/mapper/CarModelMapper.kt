package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.dto.CarModelDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CarModelMapper : EntityMapper<CarModelDTO, CarModel> {
}

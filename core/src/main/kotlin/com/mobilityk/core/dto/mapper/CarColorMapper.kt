package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.CarColor
import com.mobilityk.core.dto.CarColorDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CarColorMapper : EntityMapper<CarColorDTO, CarColor> {
}

package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.CarTrim
import com.mobilityk.core.dto.CarTrimDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CarTrimMapper : EntityMapper<CarTrimDTO, CarTrim> {
}

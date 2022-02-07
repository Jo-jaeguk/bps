package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.CarType
import com.mobilityk.core.dto.CarTypeDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CarTypeMapper : EntityMapper<CarTypeDTO, CarType> {
}

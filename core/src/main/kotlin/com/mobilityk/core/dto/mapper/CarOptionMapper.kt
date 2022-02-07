package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.CarOption
import com.mobilityk.core.dto.CarOptionDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CarOptionMapper : EntityMapper<CarOptionDTO, CarOption> {
}

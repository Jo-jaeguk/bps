package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.SellTarget
import com.mobilityk.core.dto.buyandsell.SellTargetDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SellTargetMapper : EntityMapper<SellTargetDTO, SellTarget> {
}

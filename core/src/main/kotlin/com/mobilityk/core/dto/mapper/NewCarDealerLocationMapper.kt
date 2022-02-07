package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.NewCarDealerLocation
import com.mobilityk.core.dto.NewCarDealerLocationDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NewCarDealerLocationMapper : EntityMapper<NewCarDealerLocationDTO, NewCarDealerLocation> {
}

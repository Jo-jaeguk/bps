package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.buyandsell.NewCarDealerName
import com.mobilityk.core.dto.NewCarDealerNameDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NewCarDealerNameMapper : EntityMapper<NewCarDealerNameDTO, NewCarDealerName> {
}

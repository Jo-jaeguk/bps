package com.mobilityk.core.dto.mapper

import com.mobilityk.core.domain.Notice
import com.mobilityk.core.dto.NoticeDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import java.time.LocalDateTime

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NoticeMapper : EntityMapper<NoticeDTO, Notice> {
}

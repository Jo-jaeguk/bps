package com.mobilityk.core.repository

import com.mobilityk.core.domain.MappingType
import com.mobilityk.core.domain.UploadMapping
import org.springframework.data.jpa.repository.JpaRepository

interface UploadMappingRepository: JpaRepository<UploadMapping, Long> {
    fun findAllByMappingIdAndMappingType(mappingId: Long, mappingType: MappingType): List<UploadMapping>
}
package com.mobilityk.core.service

import com.mobilityk.core.dto.CarOptionDTO
import com.mobilityk.core.dto.mapper.CarOptionMapper
import com.mobilityk.core.repository.CarOptionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class CarOptionService(
    private val carOptionRepository: CarOptionRepository,
    private val carOptionMapper: CarOptionMapper
) {
    @Transactional
    fun findAll(): List<CarOptionDTO> {
        return carOptionMapper.toDtoList(carOptionRepository.findAll())
    }

    @Transactional
    fun createCarOption(dto: CarOptionDTO): CarOptionDTO {
        return carOptionMapper.toDto(carOptionRepository.save(carOptionMapper.toEntity(dto)))
    }

    @Transactional
    fun deleteCarOption(id: Long): Unit {
        return carOptionRepository.deleteById(id)
    }
}
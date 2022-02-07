package com.mobilityk.core.service

import com.mobilityk.core.domain.CarColor
import com.mobilityk.core.dto.CarColorDTO
import com.mobilityk.core.dto.mapper.CarColorMapper
import com.mobilityk.core.repository.CarColorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class CarColorService(
    private val carColorRepository: CarColorRepository,
    private val carColorMapper: CarColorMapper
) {
    @Transactional
    fun findAll(): List<CarColorDTO> {
        return carColorMapper.toDtoList(carColorRepository.findAll())
    }

    @Transactional
    fun createCarColor(carColorDTO: CarColorDTO): CarColorDTO {
        return carColorMapper.toDto(carColorRepository.save(carColorMapper.toEntity(carColorDTO)))
    }

    @Transactional
    fun deleteCarColor(id: Long): Unit {
        return carColorRepository.deleteById(id)
    }
}
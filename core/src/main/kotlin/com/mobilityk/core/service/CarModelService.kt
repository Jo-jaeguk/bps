package com.mobilityk.core.service

import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.domain.CarModelSearchOption
import com.mobilityk.core.dto.CarModelDTO
import com.mobilityk.core.dto.api.carModel.CarModelVM
import com.mobilityk.core.dto.mapper.CarModelMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarManufacturerRepository
import com.mobilityk.core.repository.CarModelRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class CarModelService(
    private val carModelRepository: CarModelRepository,
    private val carModelMapper: CarModelMapper,
    private val carManufacturerRepository: CarManufacturerRepository
) {

    @Transactional
    fun findAllBySearchOption(searchOption: CarModelSearchOption): List<CarModelDTO> {
        return carModelRepository.findAllBySearch(searchOption)
    }

    @Transactional
    fun findAllByCarManufacturer(carManufacturerId: Long): List<CarModelDTO> {
        return carModelRepository.findAllByCarManufacturer(carManufacturerRepository.findById(carManufacturerId).get())
            .map { carModelMapper.toDto(it) }
    }

    @Transactional
    fun findAllByCarManufacturerGroupByName(searchOption: CarModelSearchOption): List<CarModelDTO> {
        return carModelRepository.findAllBySearchGroupByName(searchOption)
    }

    @Transactional
    fun findAllBySearchOption(searchOption: CarModelSearchOption, pageable: Pageable): Page<CarModelDTO> {
        return carModelRepository.findAllBySearch(searchOption, pageable)
    }

    @Transactional
    fun createCarModel(carModelVM: CarModelVM): CarModelDTO {
        val carManufacturer = carManufacturerRepository.findById(carModelVM.carManufacturerId!!).orElseThrow { CommException("not found carmanufacturer") }
        val carModelOpt = carModelRepository.findByCarManufacturerAndNameAndNameDetail(carManufacturer, carModelVM.carModelName!!,
            carModelVM.carModelNameDetail!!
        )
        return if(carModelOpt.isEmpty) {
            carModelMapper.toDto(
                carModelRepository.save(
                    CarModel(
                        carManufacturer = carManufacturer,
                        name = carModelVM.carModelName,
                        nameDetail = carModelVM.carModelNameDetail,
                        published = false
                    )
                )
            )
        } else {
            carModelMapper.toDto(carModelOpt.get())
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateCarModel(carModelId: Long, carModelVM: CarModelVM): CarModelDTO {
        val carModel = carModelRepository.findById(carModelId).orElseThrow { CommException("not found car model") }
        carModel.name = carModelVM.carModelName
        carModel.nameDetail = carModelVM.carModelNameDetail
        carModelRepository.save(carModel)
        return carModelMapper.toDto(carModel)
    }

    @Transactional
    fun deleteCarModel(carModelId: Long): Unit {
        carModelRepository.deleteById(carModelId)
    }
}
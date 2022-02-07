package com.mobilityk.core.service

import com.mobilityk.core.domain.CarClass
import com.mobilityk.core.domain.CarClassSearchOption
import com.mobilityk.core.dto.CarClassDTO
import com.mobilityk.core.dto.CarModelDTO
import com.mobilityk.core.dto.api.carClass.CarClassDetail
import com.mobilityk.core.dto.api.carClass.CarClassListDTO
import com.mobilityk.core.dto.api.carClass.CarClassVM
import com.mobilityk.core.dto.mapper.CarClassMapper
import com.mobilityk.core.dto.mapper.CarManufacturerMapper
import com.mobilityk.core.dto.mapper.CarModelMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarModelRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class CarClassService(
    private val carClassRepository: CarClassRepository,
    private val carClassMapper: CarClassMapper,
    private val carModelRepository: CarModelRepository,
    private val carModelMapper: CarModelMapper,
    private val carManufacturerMapper: CarManufacturerMapper
) {

    @Transactional
    fun findAllByCarModelId(carModelId: Long): List<CarClassDTO> {
        val carModel = carModelRepository.findById(carModelId).orElseThrow { CommException("not found carModel") }
        return carClassRepository.findAllByCarModel(carModel)
            .map { carClassMapper.toDto(it) }
    }

    @Transactional
    fun findAllByCarModel(carModelId: Long): List<CarClassDTO> {
        val carModel = carModelRepository.findById(carModelId).orElseThrow { CommException("not found carModel") }
        return carClassRepository.findAllByCarModelAndPublishedTrue(carModel)
            .map { carClassMapper.toDto(it) }
    }

    @Transactional
    fun findAllBySearchOption(searchOption: CarClassSearchOption, pageable: Pageable): Page<CarClassListDTO> {
        return carClassRepository.findAllBySearch(searchOption, pageable);
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateCarClass(carClassId: Long, carClassVM: CarClassVM): CarClassDTO {
        val carClass = carClassRepository.findById(carClassId).orElseThrow { CommException("not found car class") }
        carClass.name = carClassVM.carClassName
        carClass.carDisplacement = carClassVM.carClassDisplacement
        carClass.popularType = carClassVM.popularType
        return carClassMapper.toDto(carClassRepository.save(carClass))
    }
    @Transactional
    fun createCarClass(carClassVM: CarClassVM): CarClassDTO{

        val carModel = carModelRepository.findById(carClassVM.carModelId!!).orElseThrow { CommException("not found car model") }
        val carClassOpt = carClassRepository.findByCarModelAndNameAndCarDisplacementAndPopularType(
            carModel = carModel,
            name = carClassVM.carClassName!!,
            carDisplacement = carClassVM.carClassDisplacement!!,
            popularType = carClassVM.popularType!!
        )
        return if(carClassOpt.isEmpty) {
            carClassMapper.toDto(
                carClassRepository.save(
                    CarClass(
                        carModel = carModelRepository.findById(carClassVM.carModelId!!).get(),
                        name = carClassVM.carClassName,
                        carDisplacement = carClassVM.carClassDisplacement,
                        popularType = carClassVM.popularType,
                        published = false
                    )
                )
            )
        } else {
            carClassMapper.toDto(carClassOpt.get())
        }
    }

    @Transactional
    fun deleteById(id: Long): Unit{
        carClassRepository.deleteById(id);
    }

    @Transactional
    fun getDetail(id: Long): CarClassDetail {
        val carClass = carClassRepository.findById(id).orElseThrow { CommException("not found car class") }
        val carModelDTO = carModelMapper.toDto(carClass.carModel!!)
        val carManufacturerDTO = carManufacturerMapper.toDto(carClass.carModel?.carManufacturer!!)
        return CarClassDetail(
            id = carClass.id,
            name = carClass.name,
            carManufacturer = carManufacturerDTO,
            popularType = carClass.popularType,
            carModel = carModelDTO,
            createdAt = carClass.createdAt,
            updatedAt = carClass.updatedAt
        )
    }

    @Transactional
    fun getCarClassByModel(carModelId: Long): List<CarClassDTO> {
        val carModel = carModelRepository.findById(carModelId).orElseThrow { CommException("not found car model") }
        return carClassRepository.findAllByCarModel(carModel).map { carClassMapper.toDto(it) }
    }
}
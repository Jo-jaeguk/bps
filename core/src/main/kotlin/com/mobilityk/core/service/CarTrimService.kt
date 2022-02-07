package com.mobilityk.core.service

import com.mobilityk.core.domain.CarTrim
import com.mobilityk.core.domain.CarTrimSearchOption
import com.mobilityk.core.dto.CarTrimAdminDTO
import com.mobilityk.core.dto.CarTrimDTO
import com.mobilityk.core.dto.mapper.CarTrimMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarModelRepository
import com.mobilityk.core.repository.CarTrimRepository
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class CarTrimService(
    private val carTrimRepository: CarTrimRepository,
    private val carTrimMapper: CarTrimMapper,
    private val carClassRepository: CarClassRepository,
    private val carModelRepository: CarModelRepository
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun findAllBySearchOption(searchOption: CarTrimSearchOption, pageable: Pageable): Page<CarTrimAdminDTO>{
        return carTrimRepository.findAllBySearch(searchOption, pageable)
    }

    @Transactional
    fun findAllBySearchOption(searchOption: CarTrimSearchOption): List<CarTrimAdminDTO>{
        return carTrimRepository.findAllBySearch(searchOption)
    }

    @Transactional
    fun findByCarTrimId(id: Long): CarTrimAdminDTO {
        return carTrimRepository.findByCarTrimId(id) ?: throw CommException("not found carTrim")
    }

    @Transactional
    fun findAllByCarClassId(carClassId: Long): List<CarTrimDTO> {
        val carClass = carClassRepository.findById(carClassId).orElseThrow { CommException("not found") }
        return carTrimMapper.toDtoList(carTrimRepository.findAllByCarClass(carClass))
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createCarTrim(carClassId: Long, carTrimName: String): CarTrimDTO {
        // 차량 트림까지 다등록되어야지 클래스 , 모델의 노출여부 데이터를 업데이트 한다.
        val carClass = carClassRepository.findById(carClassId).orElseThrow { CommException("not found car class") }
        carClass.published = true

        val carModel = carModelRepository.findById(carClass.carModel?.id!!).orElseThrow { CommException("not found car model") }
        carModel.published = true

        val carTrimOpt = carTrimRepository.findByCarClassAndName(carClass, carTrimName)

        return if(carTrimOpt.isEmpty) {
            carTrimMapper.toDto(
                carTrimRepository.save(
                    CarTrim(
                        carClass = carClass,
                        name = carTrimName,
                        published = true
                    )
                )
            )
        } else {
            carTrimMapper.toDto(carTrimOpt.get())
        }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun updateCarTrim(carTrimId: Long, carTrimName: String): CarTrimDTO {
        val carTrim = carTrimRepository.findById(carTrimId).orElseThrow { CommException("not found car trim") }
        carTrim.name = carTrimName
        return carTrimMapper.toDto(carTrimRepository.save(carTrim))
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteCarTrim(carTrimId: Long): Unit {
        carTrimRepository.deleteById(carTrimId)
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun deleteCarTrimWithParent(carTrimId: Long): Unit {
        val carTrim = carTrimRepository.findById(carTrimId).orElseThrow { CommException("not found car trim") }
        val carTrimSize = carTrim.carClass?.carTrimList?.size ?: 0
        if(carTrimSize <= 1) {
            carTrimRepository.delete(carTrim)
            carClassRepository.delete(carTrim.carClass!!)
        } else {
            carTrimRepository.delete(carTrim)
        }
        /*
        val carClassOpt = carClassRepository.findById(carTrim.carClass?.id!!)
        if(carClassOpt.isPresent) {
            val carClass = carClassOpt.get()
            if(carClass.carTrimList?.size == 1) {
                carClassRepository.delete(carClass)
            }
            val carModelOpt = carModelRepository.findById(carClass.carModel?.id!!)
            if(carModelOpt.isPresent) {
                val carModel = carModelOpt.get()
                if(carModel.carClassList?.size == 1) {
                    carModelRepository.delete(carModel)
                }
            }
        }
         */
    }

}
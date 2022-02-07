package com.mobilityk.core.service

import com.mobilityk.core.domain.GuideNotification
import com.mobilityk.core.domain.GuideNotificationSearch
import com.mobilityk.core.dto.GuideNotificationDTO
import com.mobilityk.core.dto.GuidePriceDTO
import com.mobilityk.core.dto.mapper.GuideNotificationMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarManufacturerRepository
import com.mobilityk.core.repository.CarModelRepository
import com.mobilityk.core.repository.CarTrimRepository
import com.mobilityk.core.repository.GuideNotificationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class GuideNotificationService(
    private val guideNotificationRepository: GuideNotificationRepository,
    private val guideNotificationMapper: GuideNotificationMapper,
    private val carManufacturerRepository: CarManufacturerRepository,
    private val carModelRepository: CarModelRepository,
    private val carClassRepository: CarClassRepository,
    private val carTrimRepository: CarTrimRepository
) {

    @Transactional
    fun findAllBySearch(search: GuideNotificationSearch, pageable: Pageable): Page<GuideNotificationDTO> {
        return guideNotificationRepository.findAllBySearch(search, pageable)
    }

    @Transactional
    fun findById(id: Long): GuideNotificationDTO {
        val noti = guideNotificationRepository.findById(id).orElseThrow { CommException("not found noti") }
        return guideNotificationMapper.toDto(noti)
    }

    @Transactional
    fun deleteById(id: Long): Unit {
        guideNotificationRepository.deleteById(id)
    }


    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun create(writerId: Long, guideNotificationDTO: GuideNotificationDTO): GuideNotificationDTO {

        var carManufacturerName = "ALL"
        var carModelName = "ALL"
        var carModelDetailName = "ALL"
        var carClassName = "ALL"
        var carTrimName = "ALL"
        if(guideNotificationDTO.carManufacturerId != null) {
            val carManufacturer = carManufacturerRepository.findById(guideNotificationDTO.carManufacturerId!!).orElseThrow { CommException("not found") }
            carManufacturerName = carManufacturer.name!!
        }

        if(guideNotificationDTO.carModelId != null) {
            val carModel = carModelRepository.findById(guideNotificationDTO.carModelId!!).orElseThrow { CommException("not found") }
            carModelName = carModel.name!!
            carModelDetailName = carModel.nameDetail!!
        }

        if(guideNotificationDTO.carClassId != null) {
            val carClass = carClassRepository.findById(guideNotificationDTO.carClassId!!).orElseThrow { CommException("not found") }
            carClassName = carClass.name!!
        }

        if(guideNotificationDTO.carTrimId != null) {
            val carTrim = carTrimRepository.findById(guideNotificationDTO.carTrimId!!).orElseThrow { CommException("not found") }
            carTrimName = carTrim.name!!
        }

        return guideNotificationMapper.toDto(
            guideNotificationRepository.save(
                GuideNotification(
                    writerId = writerId,
                    title = guideNotificationDTO.title,
                    body = guideNotificationDTO.body,
                    carManufacturerName = carManufacturerName,
                    carManufacturerId = guideNotificationDTO.carManufacturerId,
                    carModelName = carModelName,
                    carModelId = guideNotificationDTO.carModelId,
                    carModelDetailName = carModelDetailName,
                    carClassName = carClassName,
                    carClassId = guideNotificationDTO.carClassId,
                    carTrimName = carTrimName,
                    carTrimId = guideNotificationDTO.carTrimId
                )
            )
        )
    }

}
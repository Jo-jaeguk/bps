package com.mobilityk.core.service

import com.mobilityk.core.domain.Accident
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.FuelType
import com.mobilityk.core.domain.GuideTemp
import com.mobilityk.core.domain.GuideTempOption
import com.mobilityk.core.domain.MotorType
import com.mobilityk.core.dto.GuideTempDTO
import com.mobilityk.core.dto.mapper.GuideTempMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.CarClassRepository
import com.mobilityk.core.repository.CarColorRepository
import com.mobilityk.core.repository.CarManufacturerRepository
import com.mobilityk.core.repository.CarModelRepository
import com.mobilityk.core.repository.CarTrimRepository
import com.mobilityk.core.repository.GuideTempOptionRepository
import com.mobilityk.core.repository.GuideTempRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class GuideTempService(
    private val guideTempRepository: GuideTempRepository,
    private val guideTempMapper: GuideTempMapper,
    private val carManufacturerRepository: CarManufacturerRepository,
    private val carModelRepository: CarModelRepository,
    private val carClassRepository: CarClassRepository,
    private val carTrimRepository: CarTrimRepository,
    private val carColorRepository: CarColorRepository,
    private val guideTempOptionRepository: GuideTempOptionRepository
) {

    fun findById(guideTempId: Long): GuideTempDTO {
        val guideTemp = guideTempRepository.findById(guideTempId).orElseThrow { CommException("not found") }
        return guideTempMapper.toDto(guideTemp)
    }

    fun createCarManufacturer(memberId: Long, carManufacturerId: Long, countryType: CountryType): GuideTempDTO {
        val carManufacturer = carManufacturerRepository.findById(carManufacturerId).orElseThrow { CommException("not found") }
        return guideTempMapper.toDto(
            guideTempRepository.save(
                GuideTemp(
                    memberId = memberId,
                    carManufacturer = carManufacturer.name,
                    carManufacturerId = carManufacturer.id
                )
            )
        )
    }

    @Transactional
    fun createCarModel(memberId: Long, guideTempId: Long, carModelId: Long) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        val carModel = carModelRepository.findById(carModelId).orElseThrow { CommException("not found") }
        guideTemp.carModel = carModel.name
        guideTemp.carModelDetail = carModel.nameDetail
    }


    @Transactional
    fun createCarClass(memberId: Long, guideTempId: Long, carClassId: Long) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        val carClass = carClassRepository.findById(carClassId).orElseThrow { CommException("not found") }
        guideTemp.carClassId = carClass.id
        guideTemp.carClassName = carClass.name
        guideTemp.carDisplacement = carClass.carDisplacement
    }


    @Transactional
    fun createCarTrim(memberId: Long, guideTempId: Long, carTrimId: Long) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        val carTrim = carTrimRepository.findById(carTrimId).orElseThrow { CommException("not found") }
        guideTemp.carTrim = carTrim.name
        guideTemp.carTrimId = carTrim.id
    }

    @Transactional
    fun createCarMade(memberId: Long, guideTempId: Long, year: Int, month: Int) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.carMadedYear = year
        guideTemp.carMadedMonth = month
    }

    @Transactional
    fun createAccident(memberId: Long, guideTempId: Long, accident: Accident) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.accident = accident
    }

    @Transactional
    fun createFuelType(memberId: Long, guideTempId: Long, fuelType: FuelType) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.fuelType = fuelType
    }

    @Transactional
    fun createMotorType(memberId: Long, guideTempId: Long, motorType: MotorType) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.motorType = motorType
    }

    @Transactional
    fun createCarColor(memberId: Long, guideTempId: Long, carColorId: Long) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        val carColor = carColorRepository.findById(carColorId).orElseThrow { CommException("not found") }
        guideTemp.carColor = carColor.color
    }

    @Transactional
    fun createMileage(memberId: Long, guideTempId: Long, mileage: Int) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.mileage = mileage
    }

    @Transactional
    fun deleteOptions(memberId: Long, guideTempId: Long) {
        guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTempOptionRepository.deleteAllByGuideTempIdAndMemberId(guideTempId, memberId)
    }

    @Transactional
    fun createOptions(memberId: Long, guideTempId: Long, options: List<String>?) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        options?.forEach { option ->
            guideTempOptionRepository.save(
                GuideTempOption(
                    guideTempId = guideTemp.id,
                    memberId = memberId,
                    option = option
                )
            )
        }
    }

    @Transactional
    fun createOption(memberId: Long, guideTempId: Long, option1: String?, option2: String?, option3: String?) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.option1 = option1 ?: ""
        guideTemp.option2 = option2 ?: ""
        guideTemp.option3 = option3 ?: ""
    }

    @Transactional
    fun createCarNumber(memberId: Long, guideTempId: Long, carNumber: String?) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.carNumber = carNumber ?: ""
    }

    @Transactional
    fun createCustomer(memberId: Long, guideTempId: Long, customerName: String?, customerContact: String?) {
        val guideTemp = guideTempRepository.findByIdAndMemberId(guideTempId, memberId).orElseThrow { CommException("not found") }
        guideTemp.customerName = customerName ?: ""
        guideTemp.customerContact = customerContact ?: ""
    }

}
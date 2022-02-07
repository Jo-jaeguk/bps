package com.mobilityk.core.service

import com.mobilityk.core.domain.NewCarPrice
import com.mobilityk.core.dto.BranchDTO
import com.mobilityk.core.dto.NewCarPriceDTO
import com.mobilityk.core.dto.mapper.NewCarPriceMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.NewCarPriceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class NewCarPriceService(
    private val newCarPriceRepository: NewCarPriceRepository,
    private val newCarPriceMapper: NewCarPriceMapper
) {

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun upsertNewCarPrice(
        carManufacturerName: String,
        carModelName: String,
        carModelDetailName: String,
        carTrimName: String,
        price: Long,
    ): NewCarPriceDTO {
        val newCarPriceOpt = newCarPriceRepository.findByCarManufacturerNameAndCarModelNameAndCarModelDetailNameAndCarTrimName(
            carManufacturerName,
            carModelName,
            carModelDetailName,
            carTrimName
        )
        if(newCarPriceOpt.isEmpty) {
            return newCarPriceMapper.toDto(
                newCarPriceRepository.save(
                    NewCarPrice(
                        carManufacturerName = carManufacturerName,
                        carModelName = carModelName,
                        carModelDetailName = carModelDetailName,
                        carTrimName = carTrimName
                    )
                )
            )
        } else {
            val newCarPrice = newCarPriceOpt.get()
            newCarPrice.price = price
            return newCarPriceMapper.toDto(
                newCarPriceRepository.save(
                    newCarPrice
                )
            )
        }
    }

    @Transactional(readOnly = true)
    fun getNewCarPrice(
        carManufacturerName: String,
        carModelName: String,
        carModelDetailName: String,
        carTrimName: String
    ): Long {
        val newCarPriceOpt = newCarPriceRepository.findByCarManufacturerNameAndCarModelNameAndCarModelDetailNameAndCarTrimName(
            carManufacturerName,
            carModelName,
            carModelDetailName,
            carTrimName
        )
        return if(newCarPriceOpt.isEmpty) {
            0L
        } else {
            if(newCarPriceOpt.get().price == null) 0L else newCarPriceOpt.get().price!!
        }

    }
}
package com.mobilityk.core.repository

import com.mobilityk.core.domain.NewCarPrice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface NewCarPriceRepository: JpaRepository<NewCarPrice, Long> {
    fun findByCarManufacturerNameAndCarModelNameAndCarModelDetailNameAndCarTrimName(
        carManufacturerName: String,
        carModelName: String,
        carModelDetailName: String,
        carTrimName: String,
    ): Optional<NewCarPrice>
}
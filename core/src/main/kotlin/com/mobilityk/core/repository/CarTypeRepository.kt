package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarType
import com.mobilityk.core.domain.CarTypeEnum
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CarTypeRepository: JpaRepository<CarType, Long> {
    fun findByCarType(carType: CarTypeEnum): Optional<CarType>
}
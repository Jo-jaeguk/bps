package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarColor
import org.springframework.data.jpa.repository.JpaRepository

interface CarColorRepository: JpaRepository<CarColor, Long> {
}
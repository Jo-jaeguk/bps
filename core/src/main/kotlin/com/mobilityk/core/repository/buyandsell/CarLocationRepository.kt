package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.CarLocation
import org.springframework.data.jpa.repository.JpaRepository

interface CarLocationRepository: JpaRepository<CarLocation, Long> {
    fun findAllByOrderByName(): List<CarLocation>
}
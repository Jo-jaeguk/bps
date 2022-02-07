package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarOption
import org.springframework.data.jpa.repository.JpaRepository

interface CarOptionRepository: JpaRepository<CarOption, Long> {

}
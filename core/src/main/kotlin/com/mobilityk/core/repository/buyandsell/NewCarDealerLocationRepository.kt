package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.NewCarDealerLocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface NewCarDealerLocationRepository: JpaRepository<NewCarDealerLocation, Long> {
    fun findByName(name: String): Optional<NewCarDealerLocation>
    fun findAllByOrderByName(): List<NewCarDealerLocation>
}
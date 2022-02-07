package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.BuyType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BuyTypeRepository: JpaRepository<BuyType, Long> {
    fun findByName(name: String): Optional<BuyType>
    fun findAllByOrderByName(): List<BuyType>
}
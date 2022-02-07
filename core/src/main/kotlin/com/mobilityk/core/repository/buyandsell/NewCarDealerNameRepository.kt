package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.NewCarDealerName
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface NewCarDealerNameRepository: JpaRepository<NewCarDealerName, Long> {
    fun findByName(name: String): Optional<NewCarDealerName>
    fun findAllByOrderByName(): List<NewCarDealerName>
}
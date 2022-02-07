package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.SellTarget
import org.springframework.data.jpa.repository.JpaRepository

interface SellTargetRepository: JpaRepository<SellTarget, Long> {

}
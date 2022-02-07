package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.Buy
import com.mobilityk.core.domain.buyandsell.BuyAndSell
import com.mobilityk.core.domain.buyandsell.ItemType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BuyRepository: JpaRepository<Buy, Long> {
    fun findAllByBuySell(buyAndSell: BuyAndSell): List<Buy>
    fun findByBuySellAndItemTypeAndName(buyAndSell: BuyAndSell, itemType: ItemType, name: String): Optional<Buy>
}
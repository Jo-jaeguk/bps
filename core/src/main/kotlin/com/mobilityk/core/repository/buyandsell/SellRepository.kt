package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.BuyAndSell
import com.mobilityk.core.domain.buyandsell.Sell
import org.springframework.data.jpa.repository.JpaRepository

interface SellRepository: JpaRepository<Sell, Long> {
    fun findAllByBuySellOrderByOrderIndexDesc(buyAndSell: BuyAndSell): List<Sell>
}
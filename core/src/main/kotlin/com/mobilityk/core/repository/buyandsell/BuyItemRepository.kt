package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.BuyItem
import com.mobilityk.core.domain.buyandsell.ItemType
import org.springframework.data.jpa.repository.JpaRepository

interface BuyItemRepository: JpaRepository<BuyItem, Long> {
    fun findAllByItemType(itemType: ItemType): List<BuyItem>
    fun findAllByItemTypeAndName(itemType: ItemType, name: String): List<BuyItem>
}
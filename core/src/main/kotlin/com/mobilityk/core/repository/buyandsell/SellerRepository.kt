package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.buyandsell.Seller
import org.springframework.data.jpa.repository.JpaRepository

interface SellerRepository: JpaRepository<Seller, Long> {
    fun findAllByOrderByName(): List<Seller>
}
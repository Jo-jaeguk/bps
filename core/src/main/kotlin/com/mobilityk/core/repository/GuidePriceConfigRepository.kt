package com.mobilityk.core.repository

import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.PriceConfigType
import com.mobilityk.core.domain.GuidePriceConfig
import com.mobilityk.core.domain.PopularType
import org.springframework.data.jpa.repository.JpaRepository

interface GuidePriceConfigRepository: JpaRepository<GuidePriceConfig, Long> {
    fun findByPriceConfigType(priceConfigType: PriceConfigType): GuidePriceConfig?

    fun findAllByPriceConfigType(priceConfigType: PriceConfigType): List<GuidePriceConfig>

    fun findByCountryTypeAndPriceConfigType(countryType: CountryType, priceConfigType: PriceConfigType): GuidePriceConfig?

    fun findByCountryTypeAndPriceConfigTypeAndPopularType(
        countryType: CountryType,
        priceConfigType: PriceConfigType,
        popularType: PopularType
    ): GuidePriceConfig?

}
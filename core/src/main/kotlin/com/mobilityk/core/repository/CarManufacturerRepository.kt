package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarManufacturer
import com.mobilityk.core.domain.CarManufacturerSearchOption
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.domain.QCarManufacturer
import com.mobilityk.core.domain.QCarManufacturer.carManufacturer
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CarManufacturerRepository: JpaRepository<CarManufacturer, Long>, CarManufacturerRepositoryCustom {
    fun findAllByPublishedTrue(): List<CarManufacturer>
    fun findAllByCountryType(countryType: CountryType): List<CarManufacturer>
    fun findAllByCountryTypeAndPublishedTrue(countryType: CountryType): List<CarManufacturer>
    fun findByName(name: String): Optional<CarManufacturer>
}


interface CarManufacturerRepositoryCustom {
    fun findAllBySearch(searchOption: CarManufacturerSearchOption, pageable: Pageable): Page<CarManufacturer>

    fun findAllBySearch(searchOption: CarManufacturerSearchOption): List<CarManufacturer>
}

class CarManufacturerRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): CarManufacturerRepositoryCustom {

    override fun findAllBySearch(searchOption: CarManufacturerSearchOption, pageable: Pageable): Page<CarManufacturer> {
        val fetchResults = jpaQueryFactory.selectFrom(QCarManufacturer.carManufacturer)
            .where(
                countryTypeEq(searchOption),
                countryEq(searchOption),
                publishedEq(searchOption),
                nameEq(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()

        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<CarManufacturer>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllBySearch(searchOption: CarManufacturerSearchOption): List<CarManufacturer> {
        return jpaQueryFactory.selectFrom(carManufacturer)
            .where(
                countryTypeEq(searchOption),
                countryEq(searchOption),
                publishedEq(searchOption),
                nameEq(searchOption)
            )
            .orderBy(carManufacturer.name.asc())
            .fetch()
    }

    private fun countryTypeEq(searchOption: CarManufacturerSearchOption): BooleanExpression? {
        return if(searchOption.countryType != null) {
            QCarManufacturer.carManufacturer.countryType.eq(searchOption.countryType)
        } else {
            null
        }
    }

    private fun countryEq(searchOption: CarManufacturerSearchOption): BooleanExpression? {
        return if(searchOption.country != null) {
            QCarManufacturer.carManufacturer.country.eq(searchOption.country)
        } else {
            null
        }
    }

    private fun publishedEq(searchOption: CarManufacturerSearchOption): BooleanExpression? {
        return if(searchOption.published != null) {
            QCarManufacturer.carManufacturer.published.eq(searchOption.published)
        } else {
            null
        }
    }

    private fun nameEq(searchOption: CarManufacturerSearchOption): BooleanExpression? {
        return if(!searchOption.name.isNullOrEmpty()) {
            QCarManufacturer.carManufacturer.name.eq(searchOption.name)
        } else {
            null
        }
    }


    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(CarManufacturer::class.java , carManufacturer , order.property)))
        }
        return orders
    }


}
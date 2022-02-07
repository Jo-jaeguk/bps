package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarTypeEnum
import com.mobilityk.core.domain.DamageCheckConfig
import com.mobilityk.core.domain.DamageCheckSearchOption
import com.mobilityk.core.domain.DamageLocation
import com.mobilityk.core.domain.QDamageCheckConfig.damageCheckConfig
import com.mobilityk.core.dto.DamageCheckConfigDTO
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface DamageCheckConfigRepository: JpaRepository<DamageCheckConfig, Long>, DamageCheckConfigRepositoryCustom {
    fun findByCarTypeAndDamageLocation(carType: CarTypeEnum, damageLocation: DamageLocation): Optional<DamageCheckConfig>
    fun findAllByCarType(carType: CarTypeEnum): List<DamageCheckConfig>
}


interface DamageCheckConfigRepositoryCustom {
    fun findAllBySearch(searchOption: DamageCheckSearchOption, pageable: Pageable): Page<DamageCheckConfigDTO>
}

class DamageCheckConfigRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): DamageCheckConfigRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: DamageCheckSearchOption, pageable: Pageable): Page<DamageCheckConfigDTO> {
        val fetchResults = jpaQueryFactory.select(
            Projections.bean(
                DamageCheckConfigDTO::class.java,
                damageCheckConfig.id,
                damageCheckConfig.carType,
                damageCheckConfig.damageLocation,
                damageCheckConfig.giDosaekPrice,
                damageCheckConfig.giGyoHwanPrice,
                damageCheckConfig.giPangumPrice,
                damageCheckConfig.needDosaekPrice,
                damageCheckConfig.needGyoHwanPrice,
                damageCheckConfig.needPangumPrice,
                damageCheckConfig.otherPrice,
                damageCheckConfig.updatedAt,
                damageCheckConfig.createdAt,
            )
        )
            .from(damageCheckConfig)
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()
        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<DamageCheckConfigDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    private fun search(searchOption: DamageCheckSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        if(!searchOption.search.isNullOrEmpty()) {
            builder.and(damageCheckConfig.damageLocationStr.contains(searchOption.search))
        }
        searchOption.carType?.let {
            builder.and(damageCheckConfig.carType.eq(it))
        }
        return builder
    }

    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(DamageCheckConfig::class.java , damageCheckConfig , order.property)))
        }
        return orders
    }


}
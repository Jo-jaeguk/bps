package com.mobilityk.core.repository

import com.mobilityk.core.domain.CheckListConfig
import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.QCheckListConfig
import com.mobilityk.core.domain.QGuide
import com.mobilityk.core.domain.QMember
import com.mobilityk.core.dto.CheckListConfigDTO
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

interface CheckListConfigRepository: JpaRepository<CheckListConfig, Long>, CheckListConfigRepositoryCustom {
    fun findAllByQuestionContaining(question: String, pageable: Pageable): Page<CheckListConfig>
    fun findAllByOrderIndex(orderIndex: Int): List<CheckListConfig>
    fun findAllByPublishedTrueOrderByOrderIndexAsc(): List<CheckListConfig>
}

interface CheckListConfigRepositoryCustom {
    fun findAllBySearchExcel(): List<CheckListConfigDTO>
}

class CheckListConfigRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): CheckListConfigRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearchExcel(): List<CheckListConfigDTO> {
        val fetchResults = jpaQueryFactory.select(
            Projections.bean(
                CheckListConfigDTO::class.java,
                QCheckListConfig.checkListConfig.id
            )
        )
            .from(QCheckListConfig.checkListConfig)
            .where()
            .fetch()
        return fetchResults
    }

    private fun search(searchOption: GuideSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(QGuide.guide.customerName.contains(search))
                .or(QGuide.guide.customerContact.contains(search))
                .or(QGuide.guide.memberId.like(search))
                .or(QGuide.guide.carClassName.contains(search))
                .or(QGuide.guide.carModel.contains(search))
                .or(QGuide.guide.carTrim.contains(search))
                .or(QGuide.guide.carMadedYear.like(search))
                .or(QGuide.guide.carMadedMonth.like(search))
                .or(QGuide.guide.carManufacturer.contains(search))
                .or(QMember.member.phone.contains(search))
                .or(QMember.member.name.contains(search))
                .or(QGuide.guide.carNumber.eq(search))
        }

        return builder
    }

    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(CheckListConfig::class.java , QCheckListConfig.checkListConfig , order.property)))
        }
        return orders
    }

}
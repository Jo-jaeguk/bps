package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.domain.CarModelSearchOption
import com.mobilityk.core.domain.GuideNotification
import com.mobilityk.core.domain.GuideNotificationSearch
import com.mobilityk.core.domain.QGuideNotification
import com.mobilityk.core.domain.QGuideNotification.guideNotification
import com.mobilityk.core.domain.QMember
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.GuideNotificationDTO
import com.mobilityk.core.dto.MemberDTO
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

interface GuideNotificationRepository: JpaRepository<GuideNotification, Long>, GuideNotificationRepositoryCustom {

}

interface GuideNotificationRepositoryCustom {
    fun findAllBySearch(searchOption: GuideNotificationSearch, pageable: Pageable): Page<GuideNotificationDTO>
}

class GuideNotificationRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): GuideNotificationRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: GuideNotificationSearch, pageable: Pageable): Page<GuideNotificationDTO> {
        val fetchResults = jpaQueryFactory.select(
            Projections.bean(
                GuideNotificationDTO::class.java,
                guideNotification.id,
                guideNotification.title,
                guideNotification.body,
                guideNotification.carManufacturerId,
                guideNotification.carManufacturerName,
                guideNotification.carModelId,
                guideNotification.carModelName,
                guideNotification.carModelDetailName,
                guideNotification.carClassId,
                guideNotification.carClassName,
                guideNotification.carTrimId,
                guideNotification.carTrimName,
                guideNotification.createdAt,
                guideNotification.updatedAt,
                Projections.bean(
                    MemberDTO::class.java,
                    QMember.member.id,
                    QMember.member.name,
                    QMember.member.phone,
                    QMember.member.position,
                    QMember.member.groupName,
                    QMember.member.emailAddress,
                    QMember.member.branch
                ).`as`("writer")
            )
        )
            .from(guideNotification)
            .leftJoin(QMember.member).on(guideNotification.writerId.eq(QMember.member.id))
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()
        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<GuideNotificationDTO>(
            content.toMutableList(),
            pageable, totalCount
        )

    }

    private fun search(searchOption: GuideNotificationSearch): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(guideNotification.carManufacturerName.contains(search))
                .or(guideNotification.carModelName.contains(search))
                .or(guideNotification.carModelDetailName.contains(search))
                .or(guideNotification.carClassName.contains(search))
                .or(guideNotification.carTrimName.contains(search))
        }
        if(!searchOption.carManufacturerName.isNullOrEmpty()) {
            builder.and(guideNotification.carManufacturerName.contains(searchOption.carManufacturerName))
        }
        if(!searchOption.carModelName.isNullOrEmpty()) {
            builder.and(guideNotification.carModelName.contains(searchOption.carModelName))
        }
        if(!searchOption.carModelDetailName.isNullOrEmpty()) {
            builder.and(guideNotification.carModelDetailName.contains(searchOption.carModelDetailName))
        }

        return builder
    }


    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(GuideNotification::class.java , guideNotification , order.property)))
        }
        return orders
    }


}
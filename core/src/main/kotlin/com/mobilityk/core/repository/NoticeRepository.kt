package com.mobilityk.core.repository

import com.mobilityk.core.domain.Notice
import com.mobilityk.core.domain.NoticeSearchOption
import com.mobilityk.core.domain.QNotice.notice
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository: JpaRepository<Notice, Long>, NoticeRepositoryCustom {
}


interface NoticeRepositoryCustom {
    fun findAllBySearch(searchOption: NoticeSearchOption, pageable: Pageable): Page<Notice>
}

class NoticeRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): NoticeRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: NoticeSearchOption, pageable: Pageable): Page<Notice> {
        val fetchResults = jpaQueryFactory.
            selectFrom(notice)
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()
        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<Notice>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    private fun search(searchOption: NoticeSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(notice.title.contains(search))
                .or(notice.body.contains(search))
                .or(notice.writerId.like(search))
        }
        searchOption.startedAt?.let { startedAt ->
            builder.and(notice.createdAt.goe(startedAt))
        }
        searchOption.endedAt?.let { endedAt ->
            builder.and(notice.createdAt.loe(endedAt))
        }
        return builder
    }


    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(Notice::class.java , notice , order.property)))
        }
        return orders
    }


}
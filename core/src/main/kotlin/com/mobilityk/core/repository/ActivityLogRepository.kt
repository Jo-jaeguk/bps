package com.mobilityk.core.repository

import com.mobilityk.core.domain.ActivityLog
import com.mobilityk.core.domain.QActivityLog.activityLog
import com.mobilityk.core.domain.QMember.member
import com.mobilityk.core.dto.ActivityLogDTO
import com.mobilityk.core.repository.search.ActivityLogSearchOption
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityLogRepository: JpaRepository<ActivityLog, Long>, ActivityRepositoryCustom {
}


interface ActivityRepositoryCustom {
    fun findAllBySearch(searchOption: ActivityLogSearchOption, pageable: Pageable): Page<ActivityLogDTO>
}

class ActivityRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): ActivityRepositoryCustom {

    override fun findAllBySearch(searchOption: ActivityLogSearchOption, pageable: Pageable): Page<ActivityLogDTO> {
        val fetchResults = jpaQueryFactory
            .select(
                Projections.bean(
                    ActivityLogDTO::class.java,
                    activityLog.id,
                    activityLog.writerId,
                    member.name.`as`("writerName"),
                    member.phone.`as`("writerPhone"),
                    activityLog.memberId,
                    activityLog.memberName,
                    activityLog.memberEmail,
                    activityLog.memberPhone,
                    activityLog.memberPosition,
                    activityLog.memberGroup,
                    activityLog.memberBranch,
                    activityLog.body,
                    activityLog.logType,
                    activityLog.updatedAt,
                    activityLog.createdAt
                )
            )
            .from(activityLog)
            .leftJoin(member).on(activityLog.writerId.eq(member.id))
            .where(
                search(searchOption),
                logTypeEq(searchOption),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()

        val content = fetchResults.results
        val totalCount = fetchResults.total

        return PageImpl<ActivityLogDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }


    private fun search(searchOption: ActivityLogSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(activityLog.memberName.contains(search)).or(activityLog.memberPhone.contains(search))
                .or(activityLog.memberId.like(search))
        }
        searchOption.startedAt?.let { startedAt ->
            builder.and(activityLog.createdAt.goe(startedAt))
        }
        searchOption.endedAt?.let { endedAt ->
            builder.and(activityLog.createdAt.loe(endedAt))
        }
        return builder
    }
    private fun logTypeEq(searchOption: ActivityLogSearchOption) : BooleanExpression? {
        return searchOption.logType?.let { logType ->
            activityLog.logType.eq(logType)
        }
    }
    private fun startedAtGoe(searchOption: ActivityLogSearchOption): BooleanExpression? {
        return searchOption.startedAt?.let {
            activityLog.createdAt.goe(it)
        }
    }
    private fun endedAtLoe(searchOption: ActivityLogSearchOption): BooleanExpression? {
        return searchOption.endedAt?.let {
            activityLog.createdAt.loe(it)
        }
    }
    /*
    private fun emailLike(searchOption: MemberSearchOption) : BooleanExpression? {
        return searchOption.search?.let { search ->
            QMember.member.emailAddress.like(search)
        }
    }

    private fun nameLike(searchOption: MemberSearchOption) : BooleanExpression? {
        return searchOption.search?.let { search ->
            QMember.member.name.like(search)
        }
    }

    private fun empNameLike(empName: String?) : BooleanExpression? {
        return if (!empName.isNullOrEmpty()) callHistory.empName.contains(empName) else null
    }

    private fun carNameLike(carName: String?) : BooleanExpression? {
        return if (!carName.isNullOrEmpty()) callHistory.carName.contains(carName) else null
    }

    private fun typeEq(type: String?) : BooleanExpression? {
        return if (!type.isNullOrEmpty()) callHistory.type.eq(type) else null
    }

    private fun memoLike(memo: String?) : BooleanExpression? {
        return if (!memo.isNullOrEmpty()) callHistory.type.contains(memo) else null
    }

    private fun callContentLike(callContent: String?) : BooleanExpression? {
        return if (!callContent.isNullOrEmpty()) callHistory.callContent.contains(callContent) else null
    }

    private fun inOutIn(callTypes: List<InOut>?) : BooleanExpression? {
        return if (!callTypes.isNullOrEmpty()) {
            callHistory.inOut.`in`(callTypes)
        } else {
            null
        }
    }

    private fun customerCellNumberLike(customerCellNo: String?) : BooleanExpression? {
        return if (!customerCellNo.isNullOrEmpty()) callHistory.customer.cellNumber.contains(customerCellNo) else null
    }

     */
    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(ActivityLog::class.java , activityLog , order.property)))
        }
        return orders
    }

}
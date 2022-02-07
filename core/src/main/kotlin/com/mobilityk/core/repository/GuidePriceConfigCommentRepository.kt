package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.domain.CarModelSearchOption
import com.mobilityk.core.domain.GuidePriceConfigComment
import com.mobilityk.core.domain.QCarManufacturer
import com.mobilityk.core.domain.QCarModel
import com.mobilityk.core.domain.QGuidePriceConfigComment.guidePriceConfigComment
import com.mobilityk.core.domain.QMember.member
import com.mobilityk.core.dto.GuidePriceConfigCommentDTO
import com.mobilityk.core.dto.MemberDTO
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

interface GuidePriceConfigCommentRepository: JpaRepository<GuidePriceConfigComment, Long>, GuidePriceConfigCommentRepositoryCustom {

}


interface GuidePriceConfigCommentRepositoryCustom {
    fun findAllWithMember(): List<GuidePriceConfigCommentDTO>
}

class GuidePriceConfigCommentRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): GuidePriceConfigCommentRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllWithMember(): List<GuidePriceConfigCommentDTO> {
        return jpaQueryFactory.select(
            Projections.bean(
                GuidePriceConfigCommentDTO::class.java,
                guidePriceConfigComment.id,
                guidePriceConfigComment.body,
                guidePriceConfigComment.createdAt,
                guidePriceConfigComment.updatedAt,
                Projections.bean(MemberDTO::class.java,
                    member.id,
                    member.name,
                    member.phone,
                    member.position,
                    member.groupName,
                    member.emailAddress,
                    member.branch
                ).`as`("member")
            )
        )
            .from(guidePriceConfigComment)
            .leftJoin(member).on(member.id.eq(guidePriceConfigComment.writerId))
            .orderBy(guidePriceConfigComment.createdAt.desc())
            .fetch()
    }

    private fun search(searchOption: CarModelSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(QCarModel.carModel.name.contains(search))
                .or(QCarModel.carModel.nameDetail.contains(search))
                .or(QCarModel.carModel.id.like(search))
                .or(QCarManufacturer.carManufacturer.nameKr.contains(search))
                .or(QCarManufacturer.carManufacturer.nameEng.contains(search))
        }
        searchOption.startedAt?.let { startedAt ->
            builder.and(QCarModel.carModel.createdAt.goe(startedAt))
        }
        searchOption.endedAt?.let { endedAt ->
            builder.and(QCarModel.carModel.createdAt.loe(endedAt))
        }
        return builder
    }


    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(CarModel::class.java , QCarModel.carModel , order.property)))
        }
        return orders
    }


}
package com.mobilityk.core.repository.buyandsell

import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.QGuide
import com.mobilityk.core.domain.QMember
import com.mobilityk.core.domain.buyandsell.BuyAndSell
import com.mobilityk.core.domain.buyandsell.BuyAndSellSearch
import com.mobilityk.core.domain.buyandsell.ItemType
import com.mobilityk.core.domain.buyandsell.QBuy
import com.mobilityk.core.domain.buyandsell.QBuy.buy
import com.mobilityk.core.domain.buyandsell.QBuyAndSell
import com.mobilityk.core.domain.buyandsell.QBuyAndSell.buyAndSell
import com.mobilityk.core.domain.buyandsell.QBuyItem
import com.mobilityk.core.domain.buyandsell.QBuyItem.buyItem
import com.mobilityk.core.domain.buyandsell.QSell
import com.mobilityk.core.domain.buyandsell.QSell.sell
import com.mobilityk.core.dto.buyandsell.BuyAndSellDTO
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.MemberDTO
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import javax.persistence.Column

interface BuyAndSellRepository: JpaRepository<BuyAndSell, Long>, BuyAndSellRepositoryCustom {
    fun findByGuideId(guideId: Long): Optional<BuyAndSell>
}

interface BuyAndSellRepositoryCustom {
    fun findAllBySearch(searchOption: BuyAndSellSearch, pageable: Pageable): Page<BuyAndSellDTO>
    fun findAllBySearchExcel(searchOption: BuyAndSellSearch, pageable: Pageable): Page<BuyAndSellDTO>
}

class BuyAndSellRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): BuyAndSellRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: BuyAndSellSearch, pageable: Pageable): Page<BuyAndSellDTO> {
        val guideQ = QGuide("guide")
        val buyTotalPriceTbPath = Expressions.numberPath(Long::class.java, "buyTotalPriceTb")
        val sellTotalPriceTb = Expressions.numberPath(Long::class.java, "sellTotalPriceTb")
        val bidSuccessPriceTb = Expressions.numberPath(Long::class.java, "bidSuccessPriceTb")

        val fetchResults = jpaQueryFactory.select(
            Projections.bean(
                BuyAndSellDTO::class.java,
                Projections.bean(
                    GuideDTO::class.java,
                    guideQ.id,
                    guideQ.carManufacturer,
                    guideQ.carModel,
                    guideQ.carModelDetail,
                    guideQ.carClassName,
                    guideQ.carTrim,
                    guideQ.carNumber,
                    guideQ.carColor,
                    guideQ.carMadedYear,
                    guideQ.carMadedMonth,
                    guideQ.mileage,
                    guideQ.guideStatus,
                    guideQ.carLocation,
                ).`as`("guide"),
                buyAndSell.id,
                buyAndSell.guideId,
                buyAndSell.buyerId,
                buyAndSell.buyType,
                buyAndSell.newCarDealerLocation,
                buyAndSell.newCarDealerName,
                buyAndSell.buyDate,
                buyAndSell.sellDate,
                buyAndSell.successedAt,
                buyAndSell.depositedAt,
                buyAndSell.modifyCarNumber,
                buyAndSell.seller,
                buyAndSell.carLocation,
                // 매출원가 계
                buyAndSell.sellTotalPrice,
                // 판매예상가
                buyAndSell.sellExpectPrice,
                // 판매가
                buyAndSell.sellSellPrice,
                // 판매희망가
                buyAndSell.sellHopePrice,
                // 기타 수수료 수입
                buyAndSell.sellOtherCommission,
                // 판매가 차액
                buyAndSell.sellDiffPrice,
                // 매입원가 계
                buyAndSell.buyTotalPrice,
                // 예상이익
                buyAndSell.expectRevenue,
                // 매출이익
                buyAndSell.sellRevenue,
                // 매출이익 차액
                buyAndSell.sellRevenueDiff,
                buyAndSell.carPrice,
                buyAndSell.bidSuccessPrice,
                buyAndSell.createdAt,
                buyAndSell.updatedAt,
            )
        )
            .from(buyAndSell)
            .leftJoin(guideQ).on(buyAndSell.guideId.eq(guideQ.id))
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()


        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<BuyAndSellDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllBySearchExcel(searchOption: BuyAndSellSearch, pageable: Pageable): Page<BuyAndSellDTO> {
        val guideQ = QGuide("guide")
        val memberQ = QMember("member1")
        val buyerQ = QMember("member2")

        val fetchResults = jpaQueryFactory.select(
            Projections.bean(
                BuyAndSellDTO::class.java,
                Projections.bean(
                    GuideDTO::class.java,
                    guideQ.id,
                    guideQ.carManufacturer,
                    guideQ.carModel,
                    guideQ.carModelDetail,
                    guideQ.carClassName,
                    guideQ.carTrim,
                    guideQ.carNumber,
                    guideQ.carColor,
                    guideQ.carMadedYear,
                    guideQ.carMadedMonth,
                    guideQ.mileage,
                    guideQ.guideStatus,
                    guideQ.memberId,
                ).`as`("guide"),
                Projections.bean(
                    MemberDTO::class.java,
                    memberQ.id,
                    memberQ.emailAddress,
                    memberQ.name,
                    memberQ.branch,
                    memberQ.manageBranch
                ).`as`("member"),
                Projections.bean(
                    MemberDTO::class.java,
                    buyerQ.id,
                    buyerQ.emailAddress,
                    buyerQ.name,
                    buyerQ.branch,
                    buyerQ.manageBranch
                ).`as`("buyer"),
                buyAndSell.id,
                buyAndSell.guideId,
                buyAndSell.buyerId,
                buyAndSell.buyType,
                buyAndSell.newCarDealerLocation,
                buyAndSell.newCarDealerName,
                buyAndSell.buyDate,
                buyAndSell.sellDate,
                buyAndSell.successedAt,
                buyAndSell.depositedAt,
                buyAndSell.modifyCarNumber,
                buyAndSell.seller,
                buyAndSell.carLocation,
                // 매출원가 계
                buyAndSell.sellTotalPrice,
                // 판매예상가
                buyAndSell.sellExpectPrice,
                // 판매가
                buyAndSell.sellSellPrice,
                // 판매희망가
                buyAndSell.sellHopePrice,
                // 기타 수수료 수입
                buyAndSell.sellOtherCommission,
                // 판매가 차액
                buyAndSell.sellDiffPrice,
                // 매입원가 계
                buyAndSell.buyTotalPrice,
                // 예상이익
                buyAndSell.expectRevenue,
                // 매출이익
                buyAndSell.sellRevenue,
                // 매출이익 차액
                buyAndSell.sellRevenueDiff,
                buyAndSell.carPrice,
                buyAndSell.bidSuccessPrice,
                buyAndSell.createdAt,
                buyAndSell.updatedAt,
            )
        )
            .from(buyAndSell)
            .leftJoin(guideQ).on(buyAndSell.guideId.eq(guideQ.id))
            .leftJoin(memberQ).on(guideQ.memberId.eq(memberQ.id))
            .leftJoin(buyerQ).on(buyAndSell.buyerId.eq(buyerQ.id))
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()

        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<BuyAndSellDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    private fun search(searchOption: BuyAndSellSearch): BooleanBuilder? {
        val builder = BooleanBuilder()

        if(!searchOption.carNumber.isNullOrEmpty()) {
            builder.and(
                QGuide.guide.carNumber.contains(searchOption.carNumber)
                    .or(buyAndSell.modifyCarNumber.contains(searchOption.carNumber))
            )
        }
        if(!searchOption.dealerName.isNullOrEmpty()) {
            builder.and(buyAndSell.newCarDealerName.eq(searchOption.dealerName))
        }
        if(!searchOption.carModelName.isNullOrEmpty()) {
            builder.and(QGuide.guide.carModel.contains(searchOption.carModelName))
        }
        searchOption.buyStartedAt?.let {
            builder.and(buyAndSell.buyDate.goe(it))
        }
        searchOption.buyEndedAt?.let {
            builder.and(buyAndSell.buyDate.loe(it))
        }
        searchOption.buySuccessStartedAt?.let {
            builder.and(buyAndSell.successedAt.goe(it))
        }
        searchOption.buySuccessEndedAt?.let {
            builder.and(buyAndSell.successedAt.loe(it))
        }
        return builder
    }

    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if (order.isAscending) direction = Order.ASC
            when(order.property) {
                "carManufacturer" -> { orders.add(OrderSpecifier(direction, Expressions.path(Guide::class.java, QGuide.guide, order.property))) }
                "carModel" -> { orders.add(OrderSpecifier(direction, Expressions.path(Guide::class.java, QGuide.guide, order.property))) }
                "carTrim" -> { orders.add(OrderSpecifier(direction, Expressions.path(Guide::class.java, QGuide.guide, order.property))) }
                "carNumber" -> { orders.add(OrderSpecifier(direction, Expressions.path(Guide::class.java, QGuide.guide, order.property))) }
                "guideStatus" -> { orders.add(OrderSpecifier(direction, Expressions.path(Guide::class.java, QGuide.guide, order.property))) }
                else -> { orders.add(OrderSpecifier(direction, Expressions.path(BuyAndSell::class.java, QBuyAndSell.buyAndSell, order.property))) }
            }

        }
        return orders
    }
}
package com.mobilityk.core.repository

import com.mobilityk.core.domain.EnumYn
import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuideSearchOption
import com.mobilityk.core.domain.GuideStatus
import com.mobilityk.core.domain.MarketName
import com.mobilityk.core.domain.MarketType
import com.mobilityk.core.domain.QGuide.guide
import com.mobilityk.core.domain.QGuidePrice
import com.mobilityk.core.domain.QMember
import com.mobilityk.core.domain.QMember.member
import com.mobilityk.core.dto.GuideDTO
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.util.CONST
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.QBean
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface GuideRepository: JpaRepository<Guide, Long>, GuideRepositoryCustom {
    fun countByGuideStatusAndCreatedAtBetween(guideStatus: GuideStatus, from: LocalDateTime, to: LocalDateTime): Long
    fun countByMemberId(memberId: Long): Long
    fun countByMemberIdAndGuideStatus(memberId: Long, guideStatus: GuideStatus): Long
}


interface GuideRepositoryCustom {
    fun findAllBySearch(searchOption: GuideSearchOption, pageable: Pageable): Page<GuideDTO>
    fun findAllBySearch(searchOption: GuideSearchOption): List<Guide>
    fun findAllBySearchExcel(searchOption: GuideSearchOption, pageable: Pageable): Page<GuideDTO>
    fun findAllInspectBySearchExcel(searchOption: GuideSearchOption): List<GuideDTO>
    fun findByMaxId(): Guide
    fun findByGuideId(id: Long): GuideDTO?
    fun countBySearch(searchOption: GuideSearchOption): Long
    fun getAggregation(searchOption: GuideSearchOption): MutableMap<String, Long>
    fun getTotalSellMoney(): Long
    fun findByMaxGuideIndex(startedAt: LocalDateTime, endedAt: LocalDateTime): List<Guide>

}

class GuideRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): GuideRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: GuideSearchOption, pageable: Pageable): Page<GuideDTO> {
        val fetchResults = jpaQueryFactory.select(
                Projections.bean(GuideDTO::class.java,
                    CaseBuilder().`when`(guide.adminName.isNull).then("")
                        .`when`(guide.adminName.isEmpty).then("")
                        .otherwise(guide.adminName)
                        .`as`("adminName"),
                    CaseBuilder().`when`(guide.adminPhone.isNull).then("")
                        .`when`(guide.adminPhone.isEmpty).then("")
                        .otherwise(guide.adminPhone)
                        .`as`("adminPhone"),
                    CaseBuilder().`when`(guide.customerName.isNull).then("")
                        .`when`(guide.customerName.isEmpty).then("")
                        .otherwise(guide.customerName)
                        .`as`("customerName"),
                    CaseBuilder().`when`(guide.customerContact.isNull).then("")
                        .`when`(guide.customerContact.isEmpty).then("")
                        .otherwise(guide.customerContact)
                        .`as`("customerContact"),
                    member.name.`as`("memberName"),
                    member.phone.`as`("memberPhone"),
                    guide.id,
                    guide.serial,
                    guide.memberId,
                    guide.adminId,
                    //guide.adminName,
                    //guide.adminPhone,
                    guide.adminPosition,
                    guide.sendYn,
                    guide.sendPrice,
                    guide.sellPrice,
                    guide.price,
                    guide.retailAvgPrice,
                    guide.bidAvgPrice,
                    guide.checkMinusPrice,
                    guide.damageMinusPrice,
                    guide.finalMinusPrice,
                    guide.lossPrice,
                    guide.finalBuyPrice,
                    guide.finalBuyPriceSendYn,
                    guide.country,
                    guide.carLocation,
                    guide.carColor,
                    guide.carManufacturer,
                    guide.carManufacturerId,
                    guide.carNumber,
                    guide.carModel,
                    guide.carModelDetail,
                    guide.carModelId,
                    guide.carClassName,
                    guide.carClassId,
                    guide.carTrim,
                    guide.carTrimId,
                    guide.carDisplacement,
                    guide.carMadedYear,
                    guide.carMadedMonth,
                    guide.accident,
                    guide.fuelType,
                    guide.motorType,
                    guide.guideIndex,
                    guide.mileage,
                    guide.option1,
                    guide.option2,
                    guide.option3,
                    //guide.customerName,
                    //guide.customerContact,
                    guide.guideStatus,
                    guide.inspectImgUrl,
                    guide.inspectTopImgUrl,
                    guide.inspectSideImgUrl,
                    guide.inspectBottomImgUrl,
                    guide.inspectedYn,
                    guide.evaluatorMinusPrice,
                    guide.evaluatorMinusReason,
                    guide.tireWearType,
                    guide.tireWearPrice,
                    guide.tireWearReason,
                    guide.carType,
                    guide.realMileage,
                    guide.newCarPrice,
                    guide.updatedAt,
                    guide.createdAt
                )
            )
            .from(guide)
            .leftJoin(member).on(guide.memberId.eq(member.id))
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()
        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<GuideDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllBySearchExcel(searchOption: GuideSearchOption, pageable: Pageable): Page<GuideDTO> {

        val memberQ = QMember("m1")
        val adminQ = QMember("m2")

        val fetchResults = jpaQueryFactory.select(
            Projections.bean(GuideDTO::class.java,
                Projections.bean(
                    MemberDTO::class.java,
                    memberQ.id,
                    memberQ.name,
                    memberQ.phone,
                    memberQ.emailAddress,
                    adminQ.branch,
                ).`as`("member"),
                Projections.bean(MemberDTO::class.java,
                    adminQ.id,
                    adminQ.name,
                    adminQ.phone,
                    adminQ.emailAddress,
                    adminQ.branch,
                ).`as`("customerManager"),
                memberQ.name.`as`("memberName"),
                memberQ.phone.`as`("memberPhone"),
                ExpressionUtils.`as`(
                    JPAExpressions
                        .select(QGuidePrice.guidePrice.price)
                        .from(QGuidePrice.guidePrice)
                        .where(
                            QGuidePrice.guidePrice.guide.id.eq(guide.id)
                                .and(QGuidePrice.guidePrice.marketType.eq(MarketType.RETAIL))
                                .and(QGuidePrice.guidePrice.marketNameCode.eq(MarketName.RETAIL_SK_ENCAR))
                        )
                    ,"guideEncarPrice"),
                ExpressionUtils.`as`(
                    JPAExpressions
                        .select(QGuidePrice.guidePrice.price)
                        .from(QGuidePrice.guidePrice)
                        .where(
                            QGuidePrice.guidePrice.guide.id.eq(guide.id)
                                .and(QGuidePrice.guidePrice.marketType.eq(MarketType.RETAIL))
                                .and(QGuidePrice.guidePrice.marketNameCode.eq(MarketName.RETAIL_KCAR))
                        )
                    ,"guideKcarPrice"),
                ExpressionUtils.`as`(
                    JPAExpressions
                        .select(QGuidePrice.guidePrice.price)
                        .from(QGuidePrice.guidePrice)
                        .where(
                            QGuidePrice.guidePrice.guide.id.eq(guide.id)
                                .and(QGuidePrice.guidePrice.marketType.eq(MarketType.RETAIL))
                                .and(QGuidePrice.guidePrice.marketNameCode.eq(MarketName.RETAIL_KB))
                        )
                    ,"guideKBPrice"),
                ExpressionUtils.`as`(
                    JPAExpressions
                        .select(QGuidePrice.guidePrice.price)
                        .from(QGuidePrice.guidePrice)
                        .where(
                            QGuidePrice.guidePrice.guide.id.eq(guide.id)
                                .and(QGuidePrice.guidePrice.marketType.eq(MarketType.RETAIL))
                                .and(QGuidePrice.guidePrice.marketNameCode.eq(MarketName.RETAIL_HOW))
                        )
                    ,"guideHowPrice"),
                ExpressionUtils.`as`(
                    JPAExpressions
                        .select(QGuidePrice.guidePrice.price)
                        .from(QGuidePrice.guidePrice)
                        .where(
                            QGuidePrice.guidePrice.guide.id.eq(guide.id)
                                .and(QGuidePrice.guidePrice.marketType.eq(MarketType.BIDING))
                                .and(QGuidePrice.guidePrice.marketNameCode.eq(MarketName.BID_CAR_AUCTION))
                        )
                    ,"guideCarAuctionBid"),
                ExpressionUtils.`as`(
                    JPAExpressions
                        .select(QGuidePrice.guidePrice.price)
                        .from(QGuidePrice.guidePrice)
                        .where(
                            QGuidePrice.guidePrice.guide.id.eq(guide.id)
                                .and(QGuidePrice.guidePrice.marketType.eq(MarketType.BIDING))
                                .and(QGuidePrice.guidePrice.marketNameCode.eq(MarketName.BID_KCAR))
                        )
                    ,"guideKcarBid"),
                guide.id,
                guide.serial,
                guide.memberId,
                guide.adminId,
                guide.adminName,
                guide.adminPhone,
                guide.adminPosition,
                guide.sendYn,
                guide.sendPrice,
                guide.sellPrice,
                guide.price,
                guide.retailAvgPrice,
                guide.bidAvgPrice,
                guide.checkMinusPrice,
                guide.damageMinusPrice,
                guide.finalMinusPrice,
                guide.lossPrice,
                guide.finalBuyPrice,
                guide.finalBuyPriceSendYn,
                guide.country,
                guide.carLocation,
                guide.carColor,
                guide.carManufacturer,
                guide.carManufacturerId,
                guide.carNumber,
                guide.carModel,
                guide.carModelDetail,
                guide.carModelId,
                guide.carClassName,
                guide.carClassId,
                guide.carTrim,
                guide.carTrimId,
                guide.carDisplacement,
                guide.carMadedYear,
                guide.carMadedMonth,
                guide.accident,
                guide.fuelType,
                guide.motorType,
                guide.guideIndex,
                guide.mileage,
                guide.option1,
                guide.option2,
                guide.option3,
                guide.customerName,
                guide.customerContact,
                guide.guideStatus,
                guide.inspectImgUrl,
                guide.inspectTopImgUrl,
                guide.inspectSideImgUrl,
                guide.inspectBottomImgUrl,
                guide.inspectedYn,
                guide.evaluatorMinusPrice,
                guide.evaluatorMinusReason,
                guide.tireWearType,
                guide.tireWearPrice,
                guide.tireWearReason,
                guide.carType,
                guide.realMileage,
                guide.newCarPrice,
                guide.updatedAt,
                guide.createdAt
            )
        )
            .from(guide)
            .leftJoin(memberQ).on(guide.memberId.eq(memberQ.id))
            .leftJoin(adminQ).on(memberQ.manageAdminId.eq(adminQ.id))
            .where(
                excelSearch(searchOption, memberQ)
            )
            .orderBy(guide.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()
        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<GuideDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllInspectBySearchExcel(searchOption: GuideSearchOption): List<GuideDTO> {
        val memberQ = QMember("m1")
        val adminQ = QMember("m2")

        return jpaQueryFactory.select(
            Projections.bean(GuideDTO::class.java,
                guide.id,
                guide.serial,
                guide.price,
                guide.memberId,
                guide.adminId,
                guide.adminName,
                guide.adminPhone,
                guide.adminPosition,
                guide.sendYn,
                Projections.bean(
                    MemberDTO::class.java,
                    memberQ.id,
                    memberQ.name,
                    memberQ.phone,
                    memberQ.emailAddress,
                    adminQ.branch,
                ).`as`("member"),
                Projections.bean(MemberDTO::class.java,
                    adminQ.id,
                    adminQ.name,
                    adminQ.phone,
                    adminQ.emailAddress,
                    adminQ.branch,
                ).`as`("customerManager"),
                memberQ.name.`as`("memberName"),
                memberQ.phone.`as`("memberPhone"),
                guide.country,
                guide.carLocation,
                guide.carManufacturer,
                guide.carNumber,
                guide.carModel,
                guide.carClassName,
                guide.carTrim,
                guide.carDisplacement,
                guide.carMadedYear,
                guide.carMadedMonth,
                guide.accident,
                guide.carColor,
                guide.fuelType,
                guide.motorType,
                guide.mileage,
                guide.option1,
                guide.option2,
                guide.option3,
                guide.customerName,
                guide.customerContact,
                guide.guideStatus,
                guide.finalBuyPrice,
                guide.finalMinusPrice,
                guide.finalBuyPriceSendYn,
                guide.inspectedYn,
                guide.evaluatorMinusPrice,
                guide.evaluatorMinusReason,
                guide.carType,
                guide.realMileage,
                guide.newCarPrice,
                guide.updatedAt,
                guide.createdAt
            )
        )
            .from(guide)
            .leftJoin(memberQ).on(guide.memberId.eq(memberQ.id))
            .leftJoin(adminQ).on(memberQ.manageAdminId.eq(adminQ.id))
            .where(
                search(searchOption)
            )
            .orderBy(guide.createdAt.desc())
            .fetch()
    }

    private fun getGuideDTO(): QBean<GuideDTO>? {
        return Projections.bean(GuideDTO::class.java,
            guide.id,
            guide.serial,
            guide.price,
            guide.retailAvgPrice,
            guide.bidAvgPrice,
            guide.checkMinusPrice,
            guide.lossPrice,
            guide.memberId,
            guide.adminId,
            guide.adminName,
            guide.adminPhone,
            guide.adminPosition,
            guide.sendYn,
            guide.sendPrice,
            member.name.`as`("memberName"),
            member.phone.`as`("memberPhone"),
            member.emailAddress.`as`("memberEmail"),
            member.groupName.`as`("memberGroup"),
            CaseBuilder()
                .`when`(member.branch.isNull).then("")
                .`when`(member.branch.isEmpty).then("")
                .otherwise(member.branch)
                .`as`("memberBranch"),
            CaseBuilder()
                .`when`(member.manageBranch.isNull).then("")
                .`when`(member.manageBranch.isEmpty).then("")
                .otherwise(member.manageBranch)
                .`as`("memberManageBranch"),
            guide.country,
            guide.carLocation,
            guide.carManufacturer,
            guide.carNumber,
            guide.carColor,
            guide.carModel,
            guide.carModelDetail,
            guide.carClassName,
            guide.carTrim,
            guide.carTrimId,
            guide.carDisplacement,
            guide.carMadedYear,
            guide.carMadedMonth,
            guide.accident,
            guide.fuelType,
            guide.motorType,
            guide.mileage,
            guide.option1,
            guide.option2,
            guide.option3,
            guide.customerName,
            guide.customerContact,
            guide.guideStatus,
            guide.finalBuyPrice,
            guide.finalMinusPrice,
            guide.finalBuyPriceSendYn,
            guide.inspectedYn,
            guide.carType,
            guide.realMileage,
            guide.tireWearPrice,
            guide.tireWearReason,
            guide.tireWearType,
            guide.evaluatorMinusPrice,
            guide.evaluatorMinusReason,
            guide.carType,
            guide.newCarPrice,
            guide.updatedAt,
            guide.createdAt
        )
    }

    override fun findAllBySearch(searchOption: GuideSearchOption): List<Guide> {
        return jpaQueryFactory.selectFrom(guide)
            .where(
                guideStatusEq(searchOption),
                createdAtGoe(searchOption),
                createdAtLoe(searchOption)
            )
            .orderBy(guide.createdAt.asc())
            .fetch()
    }

    override fun findByMaxId(): Guide {
        return jpaQueryFactory.selectFrom(guide)
            .orderBy(guide.id.desc())
            .fetchFirst()
    }

    override fun findByGuideId(id: Long): GuideDTO? {
        return jpaQueryFactory.select(
            Projections.bean(GuideDTO::class.java,
                guide.id,
                guide.serial,
                guide.price,
                guide.retailAvgPrice,
                guide.bidAvgPrice,
                guide.lossPrice,
                guide.memberId,
                guide.adminId,
                guide.adminName,
                guide.adminPhone,
                guide.adminPosition,
                guide.sendYn,
                guide.sendPrice,
                member.name.`as`("memberName"),
                member.phone.`as`("memberPhone"),
                member.emailAddress.`as`("memberEmail"),
                member.groupName.`as`("memberGroup"),
                CaseBuilder()
                    .`when`(member.branch.isNull).then("")
                    .`when`(member.branch.isEmpty).then("")
                    .otherwise(member.branch)
                    .`as`("memberBranch"),
                CaseBuilder()
                    .`when`(member.manageBranch.isNull).then("")
                    .`when`(member.manageBranch.isEmpty).then("")
                    .otherwise(member.manageBranch)
                    .`as`("memberManageBranch"),
                guide.country,
                guide.carLocation,
                guide.carManufacturer,
                guide.carNumber,
                guide.carColor,
                guide.carModel,
                guide.carModelDetail,
                guide.carClassName,
                guide.carTrim,
                guide.carTrimId,
                guide.carDisplacement,
                guide.carMadedYear,
                guide.carMadedMonth,
                guide.accident,
                guide.fuelType,
                guide.motorType,
                guide.mileage,
                guide.option1,
                guide.option2,
                guide.option3,
                guide.customerName,
                guide.customerContact,
                guide.guideStatus,
                guide.finalBuyPrice,
                guide.finalMinusPrice,
                guide.finalBuyPriceSendYn,
                guide.inspectedYn,
                guide.tireWearPrice,
                guide.tireWearReason,
                guide.tireWearType,
                guide.evaluatorMinusPrice,
                guide.evaluatorMinusReason,
                guide.carType,
                guide.realMileage,
                guide.checkMinusPrice,
                guide.damageMinusPrice,
                guide.inspectTopImgUrl,
                guide.inspectSideImgUrl,
                guide.inspectBottomImgUrl,
                guide.newCarPrice,
                guide.updatedAt,
                guide.createdAt
            )
        )
            .from(guide)
            .leftJoin(member).on(guide.memberId.eq(member.id))
            .where(
                guide.id.eq(id)
            )
            .fetchOne()
    }

    override fun countBySearch(searchOption: GuideSearchOption): Long {
        return jpaQueryFactory.selectFrom(guide)
            .where(
                guideStatusEq(searchOption),
                createdAtGoe(searchOption),
                createdAtLoe(searchOption)
            )
            .fetchCount()
    }


    override fun getAggregation(searchOption: GuideSearchOption): MutableMap<String, Long> {
        val fetch = jpaQueryFactory
            .select(
                guide.count(),
                guide.price.sum()
            )
            .from(guide)
            .where(
                memberIdEq(searchOption),
                guideStatusIn(searchOption),
                createdAtGoe(searchOption),
                createdAtLoe(searchOption)
            )
            .fetch()

        val tuple = fetch[0]
        val map = mutableMapOf<String, Long>()
        map.put(CONST.COUNT , tuple.get(guide.count()) ?: 0L)
        map.put(CONST.MONEY , tuple.get(guide.price.sum()) ?: 0L)
        return map
    }

    override fun getTotalSellMoney(): Long {
        val fetch = jpaQueryFactory
            .select(
                guide.price.sum()
            )
            .from(guide)
            .where(
                guide.guideStatus.`in`(GuideStatus.FINISH , GuideStatus.SELL)
            )
            .fetch()

        logger.info { "fetch result $fetch" }
        logger.info { "fetch size ${fetch.size}" }

        fetch.forEach { data ->
            logger.info { "fetch data ${data}" }
        }

        return fetch[0] ?: 0L
    }

    override fun findByMaxGuideIndex(startedAt: LocalDateTime, endedAt: LocalDateTime): List<Guide> {
        return jpaQueryFactory.
            selectFrom(guide)
            .where(
                guide.createdAt.goe(startedAt)
                    .and(guide.createdAt.loe(endedAt))
            ).orderBy(guide.guideIndex.desc())
            .fetch()
    }

    private fun search(searchOption: GuideSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(guide.customerName.contains(search))
                .or(guide.customerContact.contains(search))
                .or(guide.memberId.like(search))
                .or(guide.carClassName.contains(search))
                .or(guide.carModel.contains(search))
                .or(guide.carModelDetail.contains(search))
                .or(guide.carTrim.contains(search))
                .or(guide.carMadedYear.like(search))
                .or(guide.carMadedMonth.like(search))
                .or(guide.carManufacturer.contains(search))
                .or(member.phone.contains(search))
                .or(member.name.contains(search))
                .or(guide.carNumber.contains(search))
                .or(guide.serial.eq(search))
        }
        searchOption.startedAt?.let { startedAt ->
            builder.and(guide.createdAt.goe(startedAt))
        }
        searchOption.endedAt?.let { endedAt ->
            builder.and(guide.createdAt.loe(endedAt))
        }
        searchOption.guideStatus?.let { guideStatus ->
            builder.and(guide.guideStatus.eq(guideStatus))
        }
        searchOption.guideStatusIng?.let {
            builder.and(guide.guideStatus.notIn(GuideStatus.SELL))
                .and(guide.sendYn.ne(EnumYn.Y).or(guide.sendYn.isNull))
        }
        searchOption.memberId?.let { memberId ->
            builder.and(guide.memberId.eq(memberId))
        }
        searchOption.guideStatusList?.let { guideStatusList ->
            builder.and(guide.guideStatus.`in`(guideStatusList))
        }
        searchOption.sendYn?.let { sendYn ->
            builder.and(guide.sendYn.eq(sendYn))
        }
        searchOption.inspectedYn?.let { inspectedYn ->
            builder.and(guide.inspectedYn.eq(inspectedYn))
        }
        if(!searchOption.carManufacturerName.isNullOrEmpty()) {
            builder.and(guide.carManufacturer.contains(searchOption.carManufacturerName))
        }
        if(!searchOption.carModelName.isNullOrEmpty()) {
            builder.and(guide.carModel.contains(searchOption.carModelName))
        }
        if(!searchOption.carModelDetailName.isNullOrEmpty()) {
            builder.and(guide.carModelDetail.contains(searchOption.carModelDetailName))
        }
        if(!searchOption.carTrim.isNullOrEmpty()) {
            builder.and(guide.carTrim.contains(searchOption.carTrim))
        }
        if(!searchOption.carTrimEq.isNullOrEmpty()) {
            builder.and(guide.carTrim.eq(searchOption.carTrimEq))
        }

        return builder
    }

    private fun excelSearch(searchOption: GuideSearchOption, memberQ: QMember): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(guide.customerName.contains(search))
                .or(guide.customerContact.contains(search))
                .or(guide.memberId.like(search))
                .or(guide.carClassName.contains(search))
                .or(guide.carModel.contains(search))
                .or(guide.carTrim.contains(search))
                .or(guide.carMadedYear.like(search))
                .or(guide.carMadedMonth.like(search))
                .or(guide.carManufacturer.contains(search))
                .or(memberQ.phone.contains(search))
                .or(memberQ.name.contains(search))
                .or(guide.carNumber.eq(search))
        }
        searchOption.startedAt?.let { startedAt ->
            builder.and(guide.createdAt.goe(startedAt))
        }
        searchOption.endedAt?.let { endedAt ->
            builder.and(guide.createdAt.loe(endedAt))
        }
        searchOption.guideStatus?.let { guideStatus ->
            builder.and(guide.guideStatus.eq(guideStatus))
        }
        searchOption.guideStatusIng?.let {
            builder.and(guide.guideStatus.notIn(GuideStatus.SELL))
                .and(guide.sendYn.ne(EnumYn.Y).or(guide.sendYn.isNull))
        }
        searchOption.memberId?.let { memberId ->
            builder.and(guide.memberId.eq(memberId))
        }
        searchOption.guideStatusList?.let { guideStatusList ->
            builder.and(guide.guideStatus.`in`(guideStatusList))
        }
        searchOption.sendYn?.let { sendYn ->
            builder.and(guide.sendYn.eq(sendYn))
        }
        searchOption.inspectedYn?.let { inspectedYn ->
            builder.and(guide.inspectedYn.eq(inspectedYn))
        }
        if(!searchOption.carManufacturerName.isNullOrEmpty()) {
            builder.and(guide.carManufacturer.contains(searchOption.carManufacturerName))
        }
        if(!searchOption.carModelName.isNullOrEmpty()) {
            builder.and(guide.carModel.contains(searchOption.carModelName))
        }
        if(!searchOption.carModelDetailName.isNullOrEmpty()) {
            builder.and(guide.carModelDetail.contains(searchOption.carModelDetailName))
        }

        return builder
    }


    private fun memberIdEq(searchOption: GuideSearchOption): BooleanExpression? {
        return if(searchOption.memberId != null) {
            guide.memberId.eq(searchOption.memberId)
        } else {
            null
        }
    }

    private fun guideStatusIn(searchOption: GuideSearchOption): BooleanExpression? {
        return if(searchOption.guideStatusList != null) {
            guide.guideStatus.`in`(searchOption.guideStatusList)
        } else {
            null
        }
    }

    private fun guideStatusEq(searchOption: GuideSearchOption): BooleanExpression? {
        return if(searchOption.guideStatus != null) {
            guide.guideStatus.eq(searchOption.guideStatus)
        } else {
            null
        }
    }

    private fun createdAtGoe(searchOption: GuideSearchOption): BooleanExpression? {
        return if(searchOption.startedAt != null) {
            guide.createdAt.goe(searchOption.startedAt)
        } else {
            null
        }
    }

    private fun createdAtLoe(searchOption: GuideSearchOption): BooleanExpression? {
        return if(searchOption.endedAt != null) {
            guide.createdAt.loe(searchOption.endedAt)
        } else {
            null
        }
    }

    /*
    private fun customerCellNoLike(customerCellNo: String?) : BooleanExpression? {
        return if (!customerCellNo.isNullOrEmpty()) callHistory.customerCellNo.contains(customerCellNo) else null
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
            orders.add(OrderSpecifier(direction , Expressions.path(Guide::class.java , guide , order.property)))
        }
        return orders
    }


}
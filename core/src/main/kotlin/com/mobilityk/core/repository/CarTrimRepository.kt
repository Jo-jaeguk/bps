package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarClass
import com.mobilityk.core.domain.CarTrim
import com.mobilityk.core.domain.CarTrimSearchOption
import com.mobilityk.core.domain.QCarClass
import com.mobilityk.core.domain.QCarClass.carClass
import com.mobilityk.core.domain.QCarManufacturer.carManufacturer
import com.mobilityk.core.domain.QCarModel.carModel
import com.mobilityk.core.domain.QCarTrim.carTrim
import com.mobilityk.core.dto.CarTrimAdminDTO
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

interface CarTrimRepository: JpaRepository<CarTrim, Long>, CarTrimRepositoryCustom {
    fun findByCarClassAndName(carClass: CarClass, name: String): Optional<CarTrim>
    fun findAllByCarClass(carClass: CarClass): List<CarTrim>
}


interface CarTrimRepositoryCustom {
    fun findAllBySearch(searchOption: CarTrimSearchOption, pageable: Pageable): Page<CarTrimAdminDTO>
    fun findAllBySearch(searchOption: CarTrimSearchOption): List<CarTrimAdminDTO>
    fun findByCarTrimId(id: Long): CarTrimAdminDTO?
}

class CarTrimRepositoryCustomImpl (
    private val jpaQueryFactory: JPAQueryFactory
): CarTrimRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: CarTrimSearchOption, pageable: Pageable): Page<CarTrimAdminDTO> {
        val fetchResults = jpaQueryFactory.select(
            Projections.bean(
                CarTrimAdminDTO::class.java,
                carTrim.id,
                carTrim.name,
                carManufacturer.name.`as`("carManufacturer"),
                carManufacturer.id.`as`("carManufacturerId"),
                carModel.name.`as`("carModel"),
                carModel.nameDetail.`as`("carModelDetail"),
                carClass.name.`as`("carClass"),
                carClass.carDisplacement.`as`("carDisplacement"),
                carClass.popularType.`as`("popularType"),
                carTrim.updatedAt,
                carTrim.createdAt
            )
        )
            .from(carTrim)
            .leftJoin(carClass).on(carTrim.carClass.id.eq(carClass.id))
            .leftJoin(carModel).on(carClass.carModel.id.eq(carModel.id))
            .leftJoin(carManufacturer).on(carModel.carManufacturer.id.eq(carManufacturer.id))
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()

        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<CarTrimAdminDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllBySearch(searchOption: CarTrimSearchOption): List<CarTrimAdminDTO> {
        return jpaQueryFactory.select(
            Projections.bean(
                CarTrimAdminDTO::class.java,
                carTrim.id,
                carTrim.name,
                carManufacturer.nameKr.`as`("carManufacturer"),
                carManufacturer.id.`as`("carManufacturerId"),
                carModel.name.`as`("carModel"),
                carModel.nameDetail.`as`("carModelDetail"),
                carClass.name.`as`("carClass"),
                carClass.carDisplacement.`as`("carDisplacement"),
                carClass.popularType.`as`("popularType"),
                carTrim.updatedAt,
                carTrim.createdAt
            )
        )
            .from(carTrim)
            .leftJoin(carClass).on(carTrim.carClass.id.eq(carClass.id))
            .leftJoin(carModel).on(carClass.carModel.id.eq(carModel.id))
            .leftJoin(carManufacturer).on(carModel.carManufacturer.id.eq(carManufacturer.id))
            .where(
                search(searchOption)
            )
            .fetch()
    }

    override fun findByCarTrimId(id: Long): CarTrimAdminDTO? {
        return jpaQueryFactory.select(
            Projections.bean(
                CarTrimAdminDTO::class.java,
                carTrim.id,
                carTrim.name,
                carManufacturer.name.`as`("carManufacturer"),
                carModel.name.`as`("carModel"),
                carModel.nameDetail.`as`("carModelDetail"),
                carClass.name.`as`("carClass"),
                carClass.carDisplacement.`as`("carDisplacement"),
                carClass.popularType.`as`("popularType"),
                carTrim.updatedAt,
                carTrim.createdAt
            )
        )
            .from(carTrim)
            .leftJoin(carClass).on(carTrim.carClass.id.eq(carClass.id))
            .leftJoin(carModel).on(carClass.carModel.id.eq(carModel.id))
            .leftJoin(carManufacturer).on(carModel.carManufacturer.id.eq(carManufacturer.id))
            .where(
                carTrim.id.eq(id)
            )
            .fetchOne()
    }

    private fun search(searchOption: CarTrimSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(carClass.name.contains(search))
                .or(carModel.name.contains(search))
                .or(carTrim.name.contains(search))
                .or(carModel.nameDetail.contains(search))
                .or(carManufacturer.name.contains(search))
                .or(carManufacturer.nameEng.contains(search))
        }
        if(!searchOption.name.isNullOrEmpty()) {
            builder.and(carTrim.name.eq(searchOption.name))
        }
        searchOption.carClassId?.let {
            builder.and(carTrim.carClass.id.eq(it))
        }
        searchOption.published?.let {
            builder.and(carTrim.published.eq(it))
        }

        return builder
    }

    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if (order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction, Expressions.path(CarClass::class.java, QCarClass.carClass, order.property)))
        }
        return orders
    }
}
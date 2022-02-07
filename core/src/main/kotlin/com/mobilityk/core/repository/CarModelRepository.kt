package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarManufacturer
import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.domain.CarModelSearchOption
import com.mobilityk.core.domain.Notice
import com.mobilityk.core.domain.NoticeSearchOption
import com.mobilityk.core.domain.QCarManufacturer
import com.mobilityk.core.domain.QCarManufacturer.carManufacturer
import com.mobilityk.core.domain.QCarModel
import com.mobilityk.core.domain.QCarModel.carModel
import com.mobilityk.core.domain.QNotice
import com.mobilityk.core.dto.CarModelDTO
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

interface CarModelRepository: JpaRepository<CarModel, Long>, CarModelRepositoryCustom {
    fun findAllByCarManufacturer(carManufacturer: CarManufacturer): List<CarModel>
    fun findByCarManufacturerAndNameAndNameDetail(
        carManufacturer: CarManufacturer,
        name: String,
        nameDetail: String
    ): Optional<CarModel>
}


interface CarModelRepositoryCustom {
    fun findAllBySearch(searchOption: CarModelSearchOption, pageable: Pageable): Page<CarModelDTO>

    fun findAllBySearch(searchOption: CarModelSearchOption): List<CarModelDTO>

    fun findAllBySearchGroupByName(searchOption: CarModelSearchOption): List<CarModelDTO>
}

class CarModelRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): CarModelRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: CarModelSearchOption, pageable: Pageable): Page<CarModelDTO> {
        val fetchResults = jpaQueryFactory.
            select(
                Projections.bean(CarModelDTO::class.java,
                    carModel.id,
                    carManufacturer.name.`as`("carManufacturerName"),
                    carManufacturer.countryType.`as`("carManufacturerCountryType"),
                    carManufacturer.country.`as`("carManufacturerCountry"),
                    carModel.name,
                    carModel.nameDetail,
                    carModel.published,
                    carModel.updatedAt,
                    carModel.createdAt
                )
            )
            .from(carModel)
            .leftJoin(carManufacturer).on(carManufacturer.id.eq(carModel.carManufacturer.id))
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()
        val content = fetchResults.results
        val totalCount = fetchResults.total
        return PageImpl<CarModelDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllBySearch(searchOption: CarModelSearchOption): List<CarModelDTO> {
        val fetchResults = jpaQueryFactory.
        select(
            Projections.bean(CarModelDTO::class.java,
                carModel.id,
                /*
                carManufacturer.name.`as`("carManufacturerName"),
                carManufacturer.countryType.`as`("carManufacturerCountryType"),
                carManufacturer.country.`as`("carManufacturerCountry"),
                 */
                carModel.name,
                carModel.nameDetail,
                carModel.published,
                carModel.updatedAt,
                carModel.createdAt
            )
        )
            .from(carModel)
            .where(
                searchEq(searchOption)
            )
            .leftJoin(carManufacturer).on(carManufacturer.id.eq(carModel.carManufacturer.id))
            .fetch()
        return fetchResults
    }

    override fun findAllBySearchGroupByName(searchOption: CarModelSearchOption): List<CarModelDTO> {
        val fetchResults = jpaQueryFactory.
        select(
            Projections.bean(CarModelDTO::class.java,
                carModel.name,
                carModel.nameDetail,
                carModel.count().`as`("id")
            )
        )
            .from(carModel)
            .leftJoin(carManufacturer).on(carManufacturer.id.eq(carModel.carManufacturer.id))
            .where(
                searchEq(searchOption)
            )
            .groupBy(carModel.name)
            .fetch()
        return fetchResults
    }

    private fun search(searchOption: CarModelSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(carModel.name.contains(search))
                .or(carModel.nameDetail.contains(search))
                .or(carModel.id.like(search))
                .or(carManufacturer.nameEng.contains(search))
                .or(carManufacturer.nameKr.contains(search))
        }
        searchOption.startedAt?.let { startedAt ->
            builder.and(carModel.createdAt.goe(startedAt))
        }
        searchOption.endedAt?.let { endedAt ->
            builder.and(carModel.createdAt.loe(endedAt))
        }
        searchOption.published?.let { published ->
            builder.and(carModel.published.eq(published))
        }
        searchOption.carManufacturer?.let { carManufacturer ->
            builder.and(carModel.carManufacturer.eq(carManufacturer))
        }
        searchOption.carManufacturerId?.let {
            builder.and(carModel.carManufacturer.id.eq(it))
        }
        if(!searchOption.carModelName.isNullOrEmpty()) {
            builder.and(carModel.name.eq(searchOption.carModelName))
        }
        if(!searchOption.carModelNameDetail.isNullOrEmpty()) {
            builder.and(carModel.nameDetail.eq(searchOption.carModelNameDetail))
        }
        return builder
    }

    private fun searchEq(searchOption: CarModelSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.carManufacturer?.let {
            builder.and(carModel.carManufacturer.eq(it))
        }
        searchOption.carModelName?.let {
            builder.and(carModel.name.eq(it))
        }
        searchOption.carModelNameDetail?.let {
            builder.and(carModel.nameDetail.eq(it))
        }
        searchOption.carModelId?.let {
            builder.and(carModel.id.eq(it))
        }
        searchOption.published?.let {
            builder.and(carModel.published.eq(it))
        }

        return builder
    }

    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(CarModel::class.java , carModel , order.property)))
        }
        return orders
    }


}
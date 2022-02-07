package com.mobilityk.core.repository

import com.mobilityk.core.domain.CarClass
import com.mobilityk.core.domain.CarClassSearchOption
import com.mobilityk.core.domain.CarModel
import com.mobilityk.core.domain.PopularType
import com.mobilityk.core.domain.QCarClass
import com.mobilityk.core.domain.QCarClass.carClass
import com.mobilityk.core.domain.QCarManufacturer.carManufacturer
import com.mobilityk.core.domain.QCarModel.carModel
import com.mobilityk.core.dto.api.carClass.CarClassListDTO
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

interface CarClassRepository: JpaRepository<CarClass, Long>, CarClassRepositoryCustom {
    fun findAllByCarModel(carModel: CarModel): List<CarClass>
    fun findAllByCarModelAndPublishedTrue(carModel: CarModel): List<CarClass>
    fun findByName(name: String): Optional<CarClass>
    fun findByCarModelAndNameAndCarDisplacementAndPopularType(
        carModel: CarModel,
        name: String,
        carDisplacement: Int,
        popularType: PopularType
    ): Optional<CarClass>
}


interface CarClassRepositoryCustom {
    fun findAllBySearch(searchOption: CarClassSearchOption, pageable: Pageable): Page<CarClassListDTO>
    fun findAllBySearch(searchOption: CarClassSearchOption): List<CarClassListDTO>
}

class CarClassRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): CarClassRepositoryCustom {

    private val logger = KotlinLogging.logger {}

    override fun findAllBySearch(searchOption: CarClassSearchOption, pageable: Pageable): Page<CarClassListDTO> {
        val fetchResults = jpaQueryFactory.select(
                Projections.bean(CarClassListDTO::class.java,
                    carClass.id,
                    carClass.name,
                    carClass.popularType,
                    carClass.carDisplacement,
                    carClass.updatedAt,
                    carClass.createdAt,
                    carModel.id.`as`("carModelId"),
                    carModel.name.`as`("carModelName"),
                    carModel.nameDetail.`as`("carModelNameDetail"),
                    carModel.published.`as`("carModelPublished"),
                    carManufacturer.id.`as`("carManufacturerId"),
                    carManufacturer.name.`as`("carManufacturerName"),
                    carManufacturer.published.`as`("carManufacturerPublished"),
                    carManufacturer.countryType.`as`("carManufacturerCountryType"),
                    carManufacturer.country.`as`("carManufacturerCountry")
                )
            )
            .from(carClass)
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
        return PageImpl<CarClassListDTO>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllBySearch(searchOption: CarClassSearchOption): List<CarClassListDTO> {
        return jpaQueryFactory.select(
            Projections.bean(CarClassListDTO::class.java,
                carClass.id,
                carClass.name,
                carClass.popularType,
                carClass.updatedAt,
                carClass.createdAt,
                carModel.id.`as`("carModelId"),
                carModel.name.`as`("carModelName"),
                carModel.nameDetail.`as`("carModelNameDetail"),
                carModel.published.`as`("carModelPublished"),
                carManufacturer.id.`as`("carManufacturerId"),
                carManufacturer.name.`as`("carManufacturerName"),
                carManufacturer.published.`as`("carManufacturerPublished"),
                carManufacturer.countryType.`as`("carManufacturerCountryType"),
                carManufacturer.country.`as`("carManufacturerCountry")
            )
        )
            .from(carClass)
            .leftJoin(carModel).on(carClass.carModel.id.eq(carModel.id))
            .leftJoin(carManufacturer).on(carModel.carManufacturer.id.eq(carManufacturer.id))
            .where(
                search(searchOption)
            )
            .fetch()

    }

    private fun search(searchOption: CarClassSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(carClass.name.contains(search)).or(carModel.name.contains(search))
                .or(carModel.nameDetail.contains(search))
                .or(carManufacturer.nameKr.contains(search))
                .or(carManufacturer.nameEng.contains(search))
        }
        if(!searchOption.name.isNullOrEmpty()) {
            builder.and(carClass.name.eq(searchOption.name))
        }
        searchOption.carModelId?.let {
            builder.and(carClass.carModel.id.eq(it))
        }
        searchOption.published?.let {
            builder.and(carClass.published.eq(it))
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
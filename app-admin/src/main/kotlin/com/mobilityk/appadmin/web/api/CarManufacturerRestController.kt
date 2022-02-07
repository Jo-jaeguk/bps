package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.CarManufacturerSearchOption
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.dto.CarManufacturerDTO
import com.mobilityk.core.dto.CarModelDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.carManufacturer.CarManufacturerAllVM
import com.mobilityk.core.dto.api.carManufacturer.CarManufacturerVM
import com.mobilityk.core.dto.mapper.CarColorMapper
import com.mobilityk.core.service.CarManufacturerService
import com.mobilityk.core.service.CarModelService
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class CarManufacturerRestController(
    private val carManufacturerService: CarManufacturerService,
    private val carModelService: CarModelService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/car_manufacturer")
    fun findAll(
        @RequestParam("country_type", required = false) countryType: CountryType?,
        @RequestParam("name", required = false) name: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<ApiResponse<List<CarManufacturerDTO>>> {
        val searchOption = CarManufacturerSearchOption (
            countryType = countryType,
            name = name
        )
        return ResponseEntity.ok(
            ApiResponse<List<CarManufacturerDTO>>().apply {
                isArray = true
                val page = carManufacturerService.findAllBySearchOption(searchOption, pageable)
                val pagination = CommonUtil.convertPage(page)
                this.data = page.content
                this.pagination = pagination
            }
        )
    }

    @GetMapping("/car_manufacturer/{id}/car_model")
    fun findAllById(
        @PathVariable("id") id: Long
    ): ResponseEntity<ApiResponse<List<CarModelDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<CarModelDTO>>().apply {
                isArray = true
                this.data = carModelService.findAllByCarManufacturer(id)
            }
        )
    }

    @PostMapping("/car_manufacturer")
    fun createCarManufacturer(
        @Validated @RequestBody carManufacturerVM: CarManufacturerVM
    ): ResponseEntity<ApiResponse<CarManufacturerDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CarManufacturerDTO>().apply {
                this.data = carManufacturerService.create(carManufacturerVM)
            }
        )
    }

    @PutMapping("/car_manufacturer/{id}")
    fun updateCarManufacturer(
        @PathVariable("id") id: Long,
        @Validated @RequestBody carManufacturerVM: CarManufacturerVM
    ): ResponseEntity<ApiResponse<CarManufacturerDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<CarManufacturerDTO>().apply {
                this.data = carManufacturerService.update(id, carManufacturerVM)
            }
        )
    }

    @PutMapping("/car_manufacturer/all")
    fun updateCarManufacturerAll(
        @Validated @RequestBody carManufacturerVM: CarManufacturerAllVM
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<Unit>().apply {
                this.data = carManufacturerService.updateAll(carManufacturerVM)
            }
        )
    }

    @PutMapping("/car_manufacturer/{id}/published")
    fun publishCarManufacturer(
        @PathVariable("id") id: Long,
        @RequestBody carManufacturerVM: CarManufacturerVM
    ): ResponseEntity<ApiResponse<CarManufacturerDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<CarManufacturerDTO>().apply {
                this.data = carManufacturerService.updatePublish(id, carManufacturerVM)
            }
        )
    }

    @DeleteMapping("/car_manufacturer/{id}")
    fun deleteCarManufacturer(
        @PathVariable("id") id: Long
    ): ResponseEntity<ApiResponse<CarManufacturerDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<CarManufacturerDTO>().apply {
                this.data = carManufacturerService.delete(id)
            }
        )
    }
}
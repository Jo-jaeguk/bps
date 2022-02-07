package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.CarModelSearchOption
import com.mobilityk.core.dto.CarClassDTO
import com.mobilityk.core.dto.CarModelDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.carModel.CarModelVM
import com.mobilityk.core.service.CarClassService
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
data class CarModelRestController(
    private val carModelService: CarModelService,
    private val carClassService: CarClassService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/car_model/car_manufacturer/{carManufacturerId}")
    fun getCarModelsByCarmanufacturer(
        @PathVariable("carManufacturerId") carManufacturerId: Long,
        @RequestParam("name", required = false) name: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<ApiResponse<List<CarModelDTO>>> {
        val searchOption = CarModelSearchOption(
            carManufacturerId = carManufacturerId,
            carModelName = name
        )
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<List<CarModelDTO>>().apply {
                isArray = true
                val page = carModelService.findAllBySearchOption(searchOption, pageable)
                val pagination = CommonUtil.convertPage(page)
                this.data = page.content
                this.pagination = pagination
            }
        )
    }

    @GetMapping("/car_model/{id}/car_class")
    fun getCarClass(
        @PathVariable("id") id: Long,
    ): ResponseEntity<ApiResponse<List<CarClassDTO>>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<List<CarClassDTO>>().apply {
                isArray = true
                this.data = carClassService.findAllByCarModel(id)
            }
        )
    }



    @PostMapping("/car_model")
    fun createCarModel(
        @Validated @RequestBody carModelVM: CarModelVM
    ): ResponseEntity<ApiResponse<CarModelDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CarModelDTO>().apply {
                this.data = carModelService.createCarModel(carModelVM)
            }
        )
    }

    @PutMapping("/car_model/{carModelId}")
    fun updateCarModel(
        @PathVariable("carModelId") carModelId: Long,
        @Validated @RequestBody carModelVM: CarModelVM
    ): ResponseEntity<ApiResponse<CarModelDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<CarModelDTO>().apply {
                this.data = carModelService.updateCarModel(carModelId, carModelVM)
            }
        )
    }

    @DeleteMapping("/car_model/{carModelId}")
    fun createCarManufacturer(
        @PathVariable("carModelId") carModelId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = carModelService.deleteCarModel(carModelId)
            }
        )
    }

}
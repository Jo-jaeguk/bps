package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.CarClassSearchOption
import com.mobilityk.core.dto.CarClassDTO
import com.mobilityk.core.dto.CarModelDTO
import com.mobilityk.core.dto.CarTrimDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.carClass.CarClassDetail
import com.mobilityk.core.dto.api.carClass.CarClassListDTO
import com.mobilityk.core.dto.api.carClass.CarClassVM
import com.mobilityk.core.dto.api.carModel.CarModelVM
import com.mobilityk.core.service.CarClassService
import com.mobilityk.core.service.CarModelService
import com.mobilityk.core.service.CarTrimService
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
data class CarClassRestController(
    private val carClassService: CarClassService,
    private val carTrimService: CarTrimService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/car_class")
    fun createCarManufacturer(
        @Validated @RequestBody carClassVM: CarClassVM
    ): ResponseEntity<ApiResponse<CarClassDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CarClassDTO>().apply {
                this.data = carClassService.createCarClass(carClassVM)
            }
        )
    }

    @PutMapping("/car_class/{carClassId}")
    fun updateCarManufacturer(
        @PathVariable("carClassId") carClassId: Long,
        @Validated @RequestBody carClassVM: CarClassVM
    ): ResponseEntity<ApiResponse<CarClassDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CarClassDTO>().apply {
                this.data = carClassService.updateCarClass(carClassId, carClassVM)
            }
        )
    }

    @DeleteMapping("/car_class/{carClassId}")
    fun createCarManufacturer(
        @PathVariable("carClassId") carClassId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = carClassService.deleteById(carClassId)
            }
        )
    }

    @GetMapping("/car_class/{carClassId}")
    fun getCarClass(
        @PathVariable("carClassId") carClassId: Long
    ): ResponseEntity<ApiResponse<CarClassDetail>> {
        return ResponseEntity.ok(
            ApiResponse<CarClassDetail>().apply {
                this.data = carClassService.getDetail(carClassId)
            }
        )
    }

    @GetMapping("/car_class/{carClassId}/car_trim")
    fun getCarClassTrims(
        @PathVariable("carClassId") carClassId: Long
    ): ResponseEntity<ApiResponse<List<CarTrimDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<CarTrimDTO>>().apply {
                this.data = carTrimService.findAllByCarClassId(carClassId)
            }
        )
    }


    @GetMapping("/car_class/car_model/{carModelId}")
    fun getCarClassByCarModel(
        @PathVariable("carModelId") carModelId: Long,
        @RequestParam("name", required = false) name: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<ApiResponse<List<CarClassListDTO>>> {
        val searchOption = CarClassSearchOption(
            name = name,
            carModelId = carModelId
        )
        return ResponseEntity.ok(
            ApiResponse<List<CarClassListDTO>>().apply {
                isArray = true
                val page = carClassService.findAllBySearchOption(searchOption, pageable)
                val pagination = CommonUtil.convertPage(page)
                this.data = page.content
                this.pagination = pagination
            }
        )
    }

}
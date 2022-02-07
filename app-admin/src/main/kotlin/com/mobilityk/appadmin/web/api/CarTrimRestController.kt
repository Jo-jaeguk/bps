package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.CarTrimSearchOption
import com.mobilityk.core.dto.CarTrimAdminDTO
import com.mobilityk.core.dto.CarTrimDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.carTrim.CarTrimVM
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
data class CarTrimRestController(
    private val carTrimService: CarTrimService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/car_trim")
    fun createCarTrim(
        @Validated @RequestBody carTrimVM: CarTrimVM
    ): ResponseEntity<ApiResponse<CarTrimDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CarTrimDTO>().apply {
                this.data = carTrimService.createCarTrim(
                    carClassId = carTrimVM.carClassId!!,
                    carTrimName = carTrimVM.carTrimName!!
                )
            }
        )
    }

    @PutMapping("/car_trim/{carTrimId}")
    fun updateCarTrim(
        @PathVariable("carTrimId") carTrimId: Long,
        @Validated @RequestBody carTrimVM: CarTrimVM
    ): ResponseEntity<ApiResponse<CarTrimDTO>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse<CarTrimDTO>().apply {
                this.data = carTrimService.updateCarTrim(
                    carTrimId = carTrimId,
                    carTrimName = carTrimVM.carTrimName!!
                )
            }
        )
    }

    @GetMapping("/car_trim/{id}")
    fun getCarTrimDetail(
        @PathVariable("id") id: Long
    ): ResponseEntity<ApiResponse<CarTrimAdminDTO>> {
        return ResponseEntity.ok(
            ApiResponse<CarTrimAdminDTO>().apply {
                this.data = carTrimService.findByCarTrimId(id)
            }
        )
    }

    @GetMapping("/car_trim/car_class/{carClassId}")
    fun getCarTrimsByClass(
        @PathVariable("carClassId") carClassId: Long,
        @RequestParam("name", required = false) name: String?,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<ApiResponse<List<CarTrimAdminDTO>>> {
        val searchOption = CarTrimSearchOption(
            carClassId = carClassId,
            name = name
        )
        return ResponseEntity.ok(
            ApiResponse<List<CarTrimAdminDTO>>().apply {
                isArray = true
                val page = carTrimService.findAllBySearchOption(searchOption, pageable)
                val pagination = CommonUtil.convertPage(page)
                this.data = page.content
                this.pagination = pagination
            }
        )
    }

    @DeleteMapping("/car_trim/{carTrimId}")
    fun createCarManufacturer(
        @PathVariable("carTrimId") carTrimId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = carTrimService.deleteCarTrim(carTrimId)
            }
        )
    }

    @DeleteMapping("/car_trim/{carTrimId}/with_parent")
    fun deleteCarTrimWithParent(
        @PathVariable("carTrimId") carTrimId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = carTrimService.deleteCarTrim(carTrimId)
                //this.data = carTrimService.deleteCarTrimWithParent(carTrimId)
            }
        )
    }


}
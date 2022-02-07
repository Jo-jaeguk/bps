package com.mobilityk.appadmin.web.api

import com.mobilityk.core.dto.CarColorDTO
import com.mobilityk.core.dto.CarOptionDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.CarColorService
import com.mobilityk.core.service.CarOptionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class CarOptionRestController(
    private val carOptionService: CarOptionService
) {

    @GetMapping("/car_option")
    fun findAll(): ResponseEntity<ApiResponse<List<CarOptionDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<CarOptionDTO>>().apply {
                this.data = carOptionService.findAll()
                this.isArray = true
            }
        )
    }

    @PostMapping("/car_option")
    fun create(
        @RequestBody carOptionDTO: CarOptionDTO
    ): ResponseEntity<ApiResponse<CarOptionDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CarOptionDTO>().apply {
                this.data = carOptionService.createCarOption(carOptionDTO)
            }
        )
    }

    @DeleteMapping("/car_option/{id}")
    fun delete(
        @PathVariable("id") id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<Unit>().apply {
                this.data = carOptionService.deleteCarOption(id)
            }
        )
    }
}
package com.mobilityk.appadmin.web.api

import com.mobilityk.core.dto.CarColorDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.CarColorService
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
data class CarColorRestController(
    private val carColorService: CarColorService
) {

    @GetMapping("/car_color")
    fun findAll(): ResponseEntity<ApiResponse<List<CarColorDTO>>> {
        return ResponseEntity.ok(
            ApiResponse<List<CarColorDTO>>().apply {
                this.data = carColorService.findAll()
                this.isArray = true
            }
        )
    }

    @PostMapping("/car_color")
    fun create(
        @RequestBody carColorDTO: CarColorDTO
    ): ResponseEntity<ApiResponse<CarColorDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CarColorDTO>().apply {
                this.data = carColorService.createCarColor(carColorDTO)
            }
        )
    }

    @DeleteMapping("/car_color/{id}")
    fun delete(
        @PathVariable("id") id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<Unit>().apply {
                this.data = carColorService.deleteCarColor(id)
            }
        )
    }
}
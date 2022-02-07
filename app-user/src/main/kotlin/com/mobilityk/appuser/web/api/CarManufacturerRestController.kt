package com.mobilityk.appuser.web.api

import com.mobilityk.core.domain.CarManufacturerSearchOption
import com.mobilityk.core.domain.CountryType
import com.mobilityk.core.dto.CarManufacturerDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.CarManufacturerService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/v1")
@Validated
data class CarManufacturerRestController(
    private val carManufacturerService: CarManufacturerService
) {

    @GetMapping("/car_manufacturer")
    fun findAll(
        @RequestParam("countyType", required = false) countyType: CountryType?
    ): ResponseEntity<ApiResponse<List<CarManufacturerDTO>>> {
        val searchOption = CarManufacturerSearchOption(
            published = true,
            countryType = countyType
        )
        return ResponseEntity.ok(
            ApiResponse<List<CarManufacturerDTO>>().apply {
                isArray = true
                data = carManufacturerService.findAllBySearchOption(searchOption)
            }
        )
    }
}
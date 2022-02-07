package com.mobilityk.appadmin.web.api

import com.mobilityk.core.dto.ActivityLogDTO
import com.mobilityk.core.dto.CarColorDTO
import com.mobilityk.core.dto.DashBoardDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.ActivityLogService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.util.CommonUtil
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class DashBoardRestController(
    private val guideService: GuideService
) {

    @GetMapping("/dashboard")
    fun getDashboard(

    ): ResponseEntity<ApiResponse<DashBoardDTO>>{
        return ResponseEntity.ok(
            ApiResponse<DashBoardDTO>().apply {
                this.data = guideService.getDashBoard()
                this.result = 0
            }
        )
    }
}
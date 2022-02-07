package com.mobilityk.appadmin.web.api

import com.mobilityk.core.dto.ActivityLogDTO
import com.mobilityk.core.dto.CarColorDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.ActivityLogService
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
data class ActivityLogRestController(
    private val activityLogService: ActivityLogService
) {

    @GetMapping("/logs")
    fun getLogs(
        pageable: Pageable
    ): ResponseEntity<ApiResponse<List<ActivityLogDTO>>>{
        return ResponseEntity.ok(
            ApiResponse<List<ActivityLogDTO>>().apply {
                val page = activityLogService.findAllBy(pageable)
                this.data = page.content
                this.pagination = CommonUtil.convertPage(page)
                this.isArray = true
            }
        )
    }
}
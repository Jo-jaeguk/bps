package com.mobilityk.appadmin.web.api

import com.mobilityk.core.dto.CheckListConfigDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.CheckListConfigService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class CheckListRestController(
    private val checkListService: CheckListConfigService
) {
    private val logger = KotlinLogging.logger {}
    @PostMapping("/check_list")
    fun create(
        @RequestBody checkListDTO: CheckListConfigDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<CheckListConfigDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<CheckListConfigDTO>().apply {
                this.data = checkListService.createCheckList(checkListDTO = checkListDTO, adminId = user.username.toLong())
            }
        )
    }

    @PutMapping("/check_list/{id}")
    fun modify(
        @PathVariable("id") id: Long,
        @RequestBody checkListDTO: CheckListConfigDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<CheckListConfigDTO>> {
        return ResponseEntity.ok(
            ApiResponse<CheckListConfigDTO>().apply {
                this.data = checkListService.modifyCheckList(id, checkListDTO)
            }
        )
    }

    @PutMapping("/check_list/{id}/published")
    fun publishedModify(
        @PathVariable("id") id: Long,
        @RequestBody checkListDTO: CheckListConfigDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<CheckListConfigDTO>> {
        return ResponseEntity.ok(
            ApiResponse<CheckListConfigDTO>().apply {
                this.data = checkListService.publishModify(id, checkListDTO)
            }
        )
    }

    @PutMapping("/check_list/{id}/order/up")
    fun orderUp(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<CheckListConfigDTO>> {
        return ResponseEntity.ok(
            ApiResponse<CheckListConfigDTO>().apply {
                this.data = checkListService.orderUp(id)
            }
        )
    }

    @PutMapping("/check_list/{id}/order/down")
    fun orderDown(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<CheckListConfigDTO>> {
        return ResponseEntity.ok(
            ApiResponse<CheckListConfigDTO>().apply {
                this.data = checkListService.orderDown(id)
            }
        )
    }

    @DeleteMapping("/check_list/{id}")
    fun delete(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = checkListService.deleteCheckList(id)
            }
        )
    }

    @GetMapping("/check_list/{id}")
    fun detail(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<CheckListConfigDTO>> {
        return ResponseEntity.ok(
            ApiResponse<CheckListConfigDTO>().apply {
                this.data = checkListService.findById(id)
            }
        )
    }

}
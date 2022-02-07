package com.mobilityk.appadmin.web.api

import com.mobilityk.core.dto.NoticeDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.NoticeService
import com.mobilityk.core.service.NotificationService
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class NoticeRestController(
    private val noticeService: NoticeService,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {}
    @PostMapping("/notice")
    fun createNotice(
        @RequestBody noticeDTO: NoticeDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<NoticeDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<NoticeDTO>().apply {
                this.data = noticeService.createNotice(noticeDTO, user.username.toLong())
                notificationService.saveAllNotication(this.data?.title!!, this.data?.body!!, user.username.toLong(),
                    this.data?.id!!
                )
            }
        )
    }

    @DeleteMapping("/notice/{id}")
    fun deleteNotice(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = noticeService.deleteNotice(id)
            }
        )
    }

    @GetMapping("/notice/{id}")
    fun detail(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<NoticeDTO>> {
        return ResponseEntity.ok(
            ApiResponse<NoticeDTO>().apply {
                this.data = noticeService.findById(id)
            }
        )
    }

}
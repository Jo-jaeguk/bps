package com.mobilityk.appuser.web.api

import com.mobilityk.core.dto.NotificationDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.service.NotificationService
import com.mobilityk.core.util.CommonUtil
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/v1")
@Validated
data class NotificationRestController(
    private val memberService: MemberService,
    private val notificationService: NotificationService
) {

    @GetMapping("/notification")
    fun notifications(
        pageable: Pageable,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<NotificationDTO>>> {
        return ResponseEntity.ok(ApiResponse<List<NotificationDTO>>().apply {
            isArray = true
            val page = notificationService.findAllByMemberId(user.username.toLong(), pageable)
            this.data = page.content
            this.pagination = CommonUtil.convertPage(page)
        })
    }



}
package com.mobilityk.appuser.web.api

import com.mobilityk.core.dto.GuidePriceDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.GuidePriceService
import com.mobilityk.core.service.GuideService
import com.mobilityk.core.service.MemberService
import com.mobilityk.core.util.CommonUtil
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/v1")
@Validated
data class GuidePriceRestController(
    private val memberService: MemberService,
    private val guideService: GuideService,
    private val guidePriceService: GuidePriceService
) {

    @GetMapping("/guide_price")
    fun guidePrice(
        pageable: Pageable
    ): ResponseEntity<ApiResponse<List<GuidePriceDTO>>> {
        return ResponseEntity.ok(ApiResponse<List<GuidePriceDTO>>().apply {
            isArray = true
            val page = guidePriceService.findAll(pageable)
            this.data = page.content
            this.pagination = CommonUtil.convertPage(page)
        })
    }




}
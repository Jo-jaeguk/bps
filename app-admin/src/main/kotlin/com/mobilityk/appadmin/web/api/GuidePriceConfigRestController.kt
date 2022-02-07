package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.PriceConfigType
import com.mobilityk.core.dto.GuidePriceConfigCommentDTO
import com.mobilityk.core.dto.GuidePriceConfigDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.guidePriceConfig.EverageVM
import com.mobilityk.core.dto.api.guidePriceConfig.TableVM
import com.mobilityk.core.service.GuidePriceConfigService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/admin/v1")
@Validated
data class GuidePriceConfigRestController(
    private val guidePriceConfigService: GuidePriceConfigService
) {

    @GetMapping("/guide_price_config")
    fun guide(
        @RequestParam("priceConfigType", required = false) priceConfigType: PriceConfigType?
    ): ResponseEntity<ApiResponse<List<GuidePriceConfigDTO>>> {
        return ResponseEntity.ok(ApiResponse<List<GuidePriceConfigDTO>>().apply {
            isArray = true
            this.data = guidePriceConfigService.findAllBy(priceConfigType)
        })
    }

    @PostMapping("/guide_price_config/retail")
    fun updateRetail(
        @RequestBody guidePriceConfigDTO: GuidePriceConfigDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(ApiResponse<Unit>().apply {
            this.data = guidePriceConfigService.updateRetailConfig(user.username.toLong(),
                guidePriceConfigDTO.value?.toDouble()!!
            )
        })
    }

    @PostMapping("/guide_price_config/everage")
    fun updateEverage(
        @RequestBody everageVM: EverageVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(ApiResponse<Unit>().apply {
            this.data = guidePriceConfigService.updateEverage(user.username.toLong(), everageVM)
        })
    }

    @PostMapping("/guide_price_config/table")
    fun updateTable(
        @Valid @RequestBody tableVM: TableVM,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(ApiResponse<Unit>().apply {
            this.data = guidePriceConfigService.updatePriceTable(user.username.toLong(), tableVM)
        })
    }



    @PostMapping("/guide_price_config/comment")
    fun createComment(
        @RequestBody guidePriceConfigCommentDTO: GuidePriceConfigCommentDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuidePriceConfigCommentDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<GuidePriceConfigCommentDTO>().apply {
                this.data = guidePriceConfigService.createComment(user.username.toLong(), guidePriceConfigCommentDTO)
            }
        )
    }

    @DeleteMapping("/guide_price_config/comment/{id}")
    fun deleteComment(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = guidePriceConfigService.deleteComment(user.username.toLong(), id)
            }
        )
    }


}
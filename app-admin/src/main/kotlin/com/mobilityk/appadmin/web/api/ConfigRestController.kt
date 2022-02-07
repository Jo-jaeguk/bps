package com.mobilityk.appadmin.web.api

import com.mobilityk.core.domain.ConfigType
import com.mobilityk.core.dto.ConfigDTO
import com.mobilityk.core.dto.GuideNotificationDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.config.DamageConfigRq
import com.mobilityk.core.service.ConfigService
import com.mobilityk.core.service.DamageCheckService
import com.mobilityk.core.service.GuideNotificationService
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class ConfigRestController(
    private val configService: ConfigService,
    private val guideNotificationService: GuideNotificationService
) {
    private val logger = KotlinLogging.logger {}
    @PostMapping("/config")
    fun create(
        @RequestBody configDTO: ConfigDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<ConfigDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<ConfigDTO>().apply {
                this.data = configService.upSert(configDTO.configType!!, configDTO.value!!)
            }
        )
    }

    @GetMapping("/config")
    fun getDetail(
        @RequestParam("configType") configType: ConfigType,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<ConfigDTO>> {
        return ResponseEntity.ok(
            ApiResponse<ConfigDTO>().apply {
                this.data = configService.findByConfigType(configType = configType)
            }
        )
    }

    @GetMapping("/config/guide_notification/{guideNotificationId}")
    fun getNotiDetail(
        @PathVariable("guideNotificationId") guideNotificationId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuideNotificationDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuideNotificationDTO>().apply {
                this.data = guideNotificationService.findById(guideNotificationId)
            }
        )
    }

    @DeleteMapping("/config/guide_notification/{guideNotificationId}")
    fun deleteNoti(
        @PathVariable("guideNotificationId") guideNotificationId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = guideNotificationService.deleteById(guideNotificationId)
            }
        )
    }

    @PostMapping("/config/guide_notification")
    fun saveNoti(
        @RequestBody guideNotificationDTO: GuideNotificationDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<GuideNotificationDTO>> {
        return ResponseEntity.ok(
            ApiResponse<GuideNotificationDTO>().apply {
                this.data = guideNotificationService.create(user.username.toLong(), guideNotificationDTO)
            }
        )
    }

    @GetMapping("/config/check_list/base_price")
    fun getcheckListBasePrice(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity.ok(
            ApiResponse<String>().apply {
                this.data = configService.getCheckListBasePrice()
            }
        )
    }

    @PostMapping("/config/damage/base_ratio")
    fun saveDamageBase(
        @AuthenticationPrincipal user: User,
        @RequestBody damageConfigRq: DamageConfigRq
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = configService.saveDamageBaseRate(damageConfigRq)
            }
        )
    }

    @GetMapping("/config/damage/base_ratio")
    fun getDamageBase(
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ApiResponse<DamageConfigRq>> {
        return ResponseEntity.ok(
            ApiResponse<DamageConfigRq>().apply {
                this.data = configService.getDamageBaseRate()
            }
        )
    }

    @PostMapping("/config/damage/base_price")
    fun saveDamageBasePrice(
        @AuthenticationPrincipal user: User,
        @RequestBody damageConfigRq: DamageConfigRq
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = configService.saveDamageBasePrice(damageConfigRq)
            }
        )
    }


    @GetMapping("/config/damage/base_price")
    fun getDamageBasePrice(
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ApiResponse<DamageConfigRq>> {
        return ResponseEntity.ok(
            ApiResponse<DamageConfigRq>().apply {
                this.data = configService.getDamageBasePrice()
            }
        )
    }

    @PostMapping("/config/damage/price")
    fun saveDamagePrice(
        @AuthenticationPrincipal user: User,
        @RequestBody damageConfigRq: DamageConfigRq
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok(
            ApiResponse<Unit>().apply {
                this.data = configService.saveDamagePrice(damageConfigRq)
            }
        )
    }
}
package com.mobilityk.appadmin.web.api

import com.mobilityk.core.dto.UploadDTO
import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.service.UploadService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/admin/v1")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
data class UploadRestController(
    private val uploadService: UploadService
) {

    @PostMapping("/upload/image")
    fun uploadImage(
        @RequestParam(name = "imgFile" , required = false) imgFile: MultipartFile?,
        @AuthenticationPrincipal user: User,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<UploadDTO>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse<UploadDTO>().apply {
                this.data = uploadService.createUpload(user.username.toLong(), imgFile, httpServletRequest)
            }
        )
    }

}
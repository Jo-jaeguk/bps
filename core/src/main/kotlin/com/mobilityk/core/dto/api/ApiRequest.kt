package com.mobilityk.core.dto.api

import com.mobilityk.core.annotation.StringFormatDateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

data class ApiRequest(

    @field:NotEmpty
    @field:Size(min = 2 , max = 5)
    var name: String? = null,

    @field:PositiveOrZero
    var age: Int? = null,

    @field:Email
    var email: String? = null,

    @field:NotBlank
    var address: String? = null,

    @field:Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}\$" , message = "휴대폰 번호 형식이 아닙니다.")
    var phoneNumber: String? = null,

    @field:StringFormatDateTime(pattern = "yyyy-MM-dd HH:mm:ss" , message = "날짜 형식이 올바르지 않습니다.")
    var createdAt: String? = null

) {

    // Validation 실행될때 해당 어노테이션 메소드가 실행된다.
    // 스프링 기본 validation 어노테이션 으로 체크가 불가능할때 사용
    /*
    @AssertTrue(message = "yyyy-MM-dd HH:mm:ss 형식이여야 합니다.")
    private fun isValidCreatedAt() : Boolean {
        return try {
            LocalDateTime.parse(this.createdAt , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            true
        } catch (e: Exception) {
            false
        }
    }
     */
}
package com.mobilityk.appuser.advice

import com.mobilityk.core.dto.api.ApiResponse
import com.mobilityk.core.dto.api.ErrorDetail
import com.mobilityk.core.dto.api.ErrorResponse
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.util.CommonUtil
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.web.authentication.rememberme.CookieTheftException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

@RestControllerAdvice(basePackages = ["com.mobilityk.appuser.web.api"])
class RestControllerAdvice {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(value = [CookieTheftException::class])
    fun invalidRememberMe(
        e: CookieTheftException,
        request: HttpServletRequest,
        response: HttpServletResponse
    )  {
        logger.error { "CookieTheftException " + e.message }
        logger.error { "CookieTheftException " + CommonUtil.getExceptionPrintStack(e) }
        response.sendRedirect("/logout")
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun exception(e: java.lang.RuntimeException, request: HttpServletRequest) : ResponseEntity<ApiResponse<ErrorResponse>> {
        logger.error { "RuntimeException " + e.message }
        logger.error { "RuntimeException " + CommonUtil.getExceptionPrintStack(e) }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            getErrorResponse(mutableListOf(
                ErrorDetail(
                    message = e.message
                )),
                request, HttpStatus.INTERNAL_SERVER_ERROR.value())
        )
    }

    @ExceptionHandler(value = [CommException::class])
    fun commException(e: java.lang.RuntimeException, request: HttpServletRequest) : ResponseEntity<ApiResponse<ErrorResponse>> {
        logger.error { "CommException " + e.message }
        logger.error { "CommException " + CommonUtil.getExceptionPrintStack(e) }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            getErrorResponse(mutableListOf(
                ErrorDetail(
                    message = e.message
                )
            ), request, HttpStatus.INTERNAL_SERVER_ERROR.value())
        )
    }

    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun validationException(e: ConstraintViolationException , request: HttpServletRequest) : ResponseEntity<ApiResponse<ErrorResponse>> {
        logger.error { "ConstraintViolationException " + e.message }
        logger.error { CommonUtil.getExceptionPrintStack(e) }
        val errors = mutableListOf<ErrorDetail>()
        e.constraintViolations.forEach {
            val field = it.propertyPath.last().name
            val message = it.message
            val value = it.invalidValue
            val error = ErrorDetail().apply {
                this.field = field
                this.message = message
                this.requestedValue = value
            }
            errors.add(error)
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(errors , request, HttpStatus.BAD_REQUEST.value()))
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun methodArgumentExceptionHandler(e: MethodArgumentNotValidException , request: HttpServletRequest) : ResponseEntity<ApiResponse<ErrorResponse>> {
        val errors = mutableListOf<ErrorDetail>()
        logger.error { "MethodArgumentNotValidException " + e.message }
        logger.error { CommonUtil.getExceptionPrintStack(e) }
        e.bindingResult.allErrors.forEach { error ->
            val field = error as FieldError
            val message = error.defaultMessage
            val errorDetail = ErrorDetail().apply {
                this.field = field.field
                this.message = message
                this.requestedValue = error.rejectedValue
            }
            errors.add(errorDetail)
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(errors , request, HttpStatus.BAD_REQUEST.value()))

    }



    @ExceptionHandler(value = [IndexOutOfBoundsException::class])
    fun indexOutOfBoundException(e: IndexOutOfBoundsException): ResponseEntity<String> {
        ApiResponse<String>().apply {

        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("index error")
    }

    private fun getErrorResponse(
        errors: MutableList<ErrorDetail>,
        request: HttpServletRequest,
        httpStatusCode: Int
    ): ApiResponse<ErrorResponse> {
        val errorResponse = ErrorResponse().apply {
            this.resultCode = "Validation Fail"
            this.httpStatus = httpStatusCode
            this.message = "request data validaton error"
            this.path = request.requestURI.toString()
            this.timestamp = LocalDateTime.now()
            this.httpMethod = request.method
            this.errorDetails = errors
        }

        return ApiResponse<ErrorResponse>().apply {
            error = errorResponse
            result = httpStatusCode
            message = errorResponse.message
        }
    }
}
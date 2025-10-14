package com.abuamar.user_management_service.exception

import com.abuamar.user_management_service.domain.dto.res.BaseResponse
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<BaseResponse<Map<String, String?>>> {
        val errorMessages = mutableMapOf<String, String?>()

        e.bindingResult.fieldErrors.forEach { error ->
            errorMessages[error.field] = error.defaultMessage
        }

        return ResponseEntity(
            BaseResponse(
                success = false,
                message = "Validation failed",
                data = errorMessages
            ),
            HttpStatus.BAD_REQUEST
        )
    }
    
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException
    ): ResponseEntity<BaseResponse<Map<String, String?>>> {
        val errorMessages = mutableMapOf<String, String?>()
        
        // Check if it's a missing parameter error
        val cause = e.cause
        if (cause is MissingKotlinParameterException) {
            val fieldName = cause.parameter.name ?: "unknown"
            errorMessages[fieldName] = "Field '$fieldName' is required and cannot be missing"
            
            return ResponseEntity(
                BaseResponse(
                    success = false,
                    message = "Required field is missing",
                    data = errorMessages
                ),
                HttpStatus.BAD_REQUEST
            )
        }
        
        // For other JSON parsing errors
        return ResponseEntity(
            BaseResponse(
                success = false,
                message = "Invalid request body: ${e.message?.substringBefore(";") ?: "Malformed JSON"}"
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        e: CustomException
    ): ResponseEntity<BaseResponse<Any?>> {
        e.printStackTrace()
        return ResponseEntity(
            BaseResponse(
                success = false,
                message = e.message,
                data = e.data
            ),
            HttpStatus.valueOf(e.statusCode)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception
    ): ResponseEntity<BaseResponse<Any?>> {
        e.printStackTrace()
        return ResponseEntity(
            BaseResponse(
                success = false,
                message = e.message ?: "Internal Server Error"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
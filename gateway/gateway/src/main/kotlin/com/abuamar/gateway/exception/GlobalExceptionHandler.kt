package com.abuamar.gateway.exception

import com.abuamar.gateway.domain.dto.res.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<BaseResponse<Any?>> {
        val errorMessages = mutableListOf<String?>()

        e.bindingResult.fieldErrors.forEach { error ->
            errorMessages.add("${error.field}: ${error.defaultMessage}")
        }

        return ResponseEntity(
            BaseResponse(
                success = false,
                message = errorMessages.joinToString(", "),
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        e: CustomException
    ): ResponseEntity<BaseResponse<Any?>> {
        print(e.printStackTrace())
        return ResponseEntity(
            BaseResponse(
                success = false,
                message = e.message
            ),
            HttpStatus.valueOf(e.statusCode)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception
    ): ResponseEntity<BaseResponse<Any?>> {
        return ResponseEntity(
            BaseResponse(
                success = false,
                message = e.message ?: "Internal Server Error"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
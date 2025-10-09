package com.abuamar.user_management_service.controller

import com.abuamar.user_management_service.domain.dto.req.ReqLogin
import com.abuamar.user_management_service.domain.dto.res.BaseResponse
import com.abuamar.user_management_service.domain.dto.res.ResUserById
import com.abuamar.user_management_service.domain.dto.req.ReqRegister
import com.abuamar.user_management_service.domain.dto.res.ResLogin
import com.abuamar.user_management_service.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(
        @RequestBody @Valid req: ReqRegister
    ): ResponseEntity<BaseResponse<ResUserById>> {
        return ResponseEntity(
            BaseResponse(
                success = true,
                message = "Success register user",
                data = authService.register(req),
            ),
            HttpStatus.CREATED
        )
    }

    @PostMapping("/login")
    fun login(
        @RequestBody req: ReqLogin
    ): ResponseEntity<BaseResponse<ResLogin>> {
        return ResponseEntity(
            BaseResponse(
                success = true,
                message = "Success login",
                data = authService.login(req),
            ),
            HttpStatus.OK
        )
    }
}
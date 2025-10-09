package com.abuamar.user_management_service.domain.dto.req

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ReqLogin(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String,
)

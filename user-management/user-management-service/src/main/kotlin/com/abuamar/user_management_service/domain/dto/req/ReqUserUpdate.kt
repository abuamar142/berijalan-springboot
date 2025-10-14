package com.abuamar.user_management_service.domain.dto.req

import jakarta.validation.constraints.Size

data class ReqUserUpdate(
    var id: Int,
    
    @field:Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    val username: String?,
    
    @field:Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    val fullName: String?
)
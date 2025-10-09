package com.abuamar.user_management_service.domain.dto.res

import java.util.UUID

data class BaseResponse<T>(
    val reqId: UUID = UUID.randomUUID(),
    val success: Boolean = false,
    val message: String,
    val data: T? = null,
)

package com.abuamar.gateway.domain.dto.res

import java.util.UUID

data class BaseResponse<T>(
    val reqId: UUID = UUID.randomUUID(),
    val success: Boolean = true,
    val message: String = "Success",
    val data: T? = null,
)
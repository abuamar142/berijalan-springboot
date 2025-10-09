package com.abuamar.user_management_service.exception

import java.lang.RuntimeException

class CustomException(
    override val message: String,
    val statusCode: Int,
    val success: Boolean = false,
    val data: Any? = { }
): RuntimeException(message) {
}
package com.abuamar.gateway.exception

import java.lang.RuntimeException

class CustomException(
    override val message: String,
    val statusCode: Int,
    val data: Any? = null
): RuntimeException(message) {
}
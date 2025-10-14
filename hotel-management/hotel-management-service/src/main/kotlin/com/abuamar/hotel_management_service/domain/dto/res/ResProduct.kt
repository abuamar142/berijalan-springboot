package com.abuamar.hotel_management_service.domain.dto.res

import java.io.Serializable
import java.sql.Timestamp

data class ResProduct(
    val id: Int,
    val name: String,
    val price: Long,
    val brandName: String,
    val updatedBy: String? = null,
    val createdAt: Timestamp,
    val createdBy: String,
): Serializable
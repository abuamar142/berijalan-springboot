package com.abuamar.hotel_management_service.domain.dto.res

import java.sql.Timestamp

data class ResUserById(
    val id: Int,
    val username: String,
    val fullName: String,
    val roleName: String? = null,
    val createdAt: Timestamp,
    val createdBy: String,
)

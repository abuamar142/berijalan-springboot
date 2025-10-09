package com.abuamar.user_management_service.domain.dto.res

import java.sql.Timestamp

data class ResUser(
    val id: Int,
    val username: String,
    val fullName: String,
    val createdAt: Timestamp,
    val createdBy: String,
)

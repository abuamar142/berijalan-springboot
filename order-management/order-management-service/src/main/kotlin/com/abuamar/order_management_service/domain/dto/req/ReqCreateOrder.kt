package com.abuamar.order_management_service.domain.dto.req

import jakarta.validation.constraints.*
import java.sql.Date

data class ReqCreateOrder(
    @field:NotNull(message = "User ID is required")
    @field:Positive(message = "User ID must be positive")
    val userId: Int,

    @field:NotNull(message = "Room ID is required")
    @field:Positive(message = "Room ID must be positive")
    val roomId: Int,

    @field:NotNull(message = "Check-in date is required")
    val checkInDate: Date,

    @field:NotNull(message = "Check-out date is required")
    val checkOutDate: Date,

    @field:NotNull(message = "Guest count is required")
    @field:Positive(message = "Guest count must be positive")
    val guestCount: Int,

    @field:Size(max = 1000, message = "Special requests cannot exceed 1000 characters")
    val specialRequests: String? = null
)

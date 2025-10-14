package com.abuamar.order_management_service.domain.dto.res

import java.math.BigDecimal
import java.sql.Timestamp

data class ResOrder(
    val id: Int,
    val orderNumber: String,
    val userId: Int,
    val roomNumber: String,
    val hotelName: String,
    val checkInDate: Timestamp,
    val checkOutDate: Timestamp,
    val totalAmount: BigDecimal,
    val status: String,
    val guestCount: Int,
    val specialRequests: String?,
    val paymentStatus: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
)
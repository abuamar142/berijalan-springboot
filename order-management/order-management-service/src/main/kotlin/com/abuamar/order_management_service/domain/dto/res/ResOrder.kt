package com.abuamar.order_management_service.domain.dto.res

import com.abuamar.order_management_service.domain.enum.OrderStatus
import com.abuamar.order_management_service.domain.enum.PaymentStatus
import java.sql.Date
import java.sql.Timestamp

data class ResOrder(
    val id: Int,
    val orderNumber: String,
    val user: ResUser,
    val room: ResRoom,
    val checkInDate: Date,
    val checkOutDate: Date,
    val nights: Int,
    val guestCount: Int,
    val totalAmount: Int,
    val specialRequests: String?,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val createdBy: String,
    val updatedBy: String
)
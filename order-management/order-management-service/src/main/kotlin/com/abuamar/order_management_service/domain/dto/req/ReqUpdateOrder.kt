package com.abuamar.order_management_service.domain.dto.req

import com.abuamar.order_management_service.domain.enum.OrderStatus
import com.abuamar.order_management_service.domain.enum.PaymentStatus
import jakarta.validation.constraints.*
import java.sql.Date

data class ReqUpdateOrder(
    @field:Positive(message = "Room ID must be positive")
    val roomId: Int? = null,

    val checkInDate: Date? = null,

    val checkOutDate: Date? = null,

    @field:Positive(message = "Guest count must be positive")
    val guestCount: Int? = null,

    @field:Size(max = 1000, message = "Special requests cannot exceed 1000 characters")
    val specialRequests: String? = null,

    val status: OrderStatus? = null,

    val paymentStatus: PaymentStatus? = null
)

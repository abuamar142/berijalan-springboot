package com.abuamar.order_management_service.domain.dto.req

import com.abuamar.order_management_service.domain.enum.PaymentMethod
import jakarta.validation.constraints.*
import java.sql.Timestamp

data class ReqCreatePayment(
    @field:NotNull(message = "Order ID is required")
    @field:Positive(message = "Order ID must be positive")
    val orderId: Int,

    @field:NotNull(message = "Payment method is required")
    val paymentMethod: PaymentMethod,

    @field:NotNull(message = "Payment amount is required")
    @field:Positive(message = "Payment amount must be positive")
    val paymentAmount: Int,

    @field:NotNull(message = "Payment date is required")
    val paymentDate: Timestamp,

    @field:Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    val transactionId: String? = null,

    @field:Size(max = 100, message = "Payment reference cannot exceed 100 characters")
    val paymentReference: String? = null,

    @field:Size(max = 50, message = "Payment gateway cannot exceed 50 characters")
    val paymentGateway: String? = null,

    @field:Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    val notes: String? = null
)

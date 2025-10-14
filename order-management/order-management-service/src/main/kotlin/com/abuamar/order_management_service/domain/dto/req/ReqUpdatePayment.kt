package com.abuamar.order_management_service.domain.dto.req

import com.abuamar.order_management_service.domain.enum.PaymentMethod
import com.abuamar.order_management_service.domain.enum.TransactionPaymentStatus
import jakarta.validation.constraints.*
import java.sql.Timestamp

data class ReqUpdatePayment(
    @field:Positive(message = "Payment amount must be positive")
    val paymentAmount: Int? = null,

    val paymentDate: Timestamp? = null,

    val paymentMethod: PaymentMethod? = null,

    @field:Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    val transactionId: String? = null,

    @field:Size(max = 100, message = "Payment reference cannot exceed 100 characters")
    val paymentReference: String? = null,

    val paymentStatus: TransactionPaymentStatus? = null,

    @field:Size(max = 50, message = "Payment gateway cannot exceed 50 characters")
    val paymentGateway: String? = null,

    @field:Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    val notes: String? = null
)

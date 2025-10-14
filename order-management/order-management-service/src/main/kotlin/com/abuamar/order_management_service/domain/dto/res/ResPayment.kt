package com.abuamar.order_management_service.domain.dto.res

import com.abuamar.order_management_service.domain.enum.PaymentMethod
import com.abuamar.order_management_service.domain.enum.TransactionPaymentStatus
import java.sql.Timestamp

data class ResPayment(
    val id: Int,
    val orderId: Int,
    val orderNumber: String?,
    val paymentMethod: PaymentMethod,
    val paymentAmount: Int,
    val paymentDate: Timestamp,
    val transactionId: String?,
    val paymentReference: String?,
    val paymentStatus: TransactionPaymentStatus,
    val paymentGateway: String?,
    val notes: String?,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val createdBy: String,
    val updatedBy: String
)

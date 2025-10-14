package com.abuamar.order_management_service.domain.enum

// For Order payment status
enum class OrderPaymentStatus {
    UNPAID,
    PARTIAL,
    PAID,
    REFUNDED
}

// For individual payment transaction status
enum class TransactionPaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED
}

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
    PENDING,   // Default when customer creates payment (awaiting admin approval)
    SUCCESS,   // Admin approved payment (counts toward order total)
    FAILED     // Admin rejected payment (does not count)
}

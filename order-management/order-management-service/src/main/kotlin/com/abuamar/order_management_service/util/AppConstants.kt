package com.abuamar.order_management_service.util

object AppConstants {
    const val SYSTEM_USER = "SYSTEM"

    // Header Constants
    const val HEADER_USER_ID = "X-USER-ID"
    const val HEADER_USER_AUTHORITY = "X-USER-AUTHORITY"
    
    // Role Constants
    const val ROLE_ADMIN = "admin"
    const val ROLE_USER = "user"
    
    // Error Messages - Authentication
    const val ERR_AUTH_HEADER_MISSING = "Authentication header is missing"
    const val ERR_UNAUTHORIZED = "You are not authorized to perform this action"
    
    // Error Messages - Order
    const val ERR_ORDER_NOT_FOUND = "Order not found"
    const val ERR_NO_ORDERS_FOUND = "No orders found"
    const val ERR_ORDER_ALREADY_ACTIVE = "Order is already active"
    const val ERR_INVALID_DATE_RANGE = "Check-out date must be after check-in date"
    const val ERR_ROOM_NOT_AVAILABLE = "Room is not available for the selected dates"
    const val ERR_GUEST_COUNT_EXCEEDS = "Guest count exceeds room capacity"
    const val ERR_ROOM_STATUS_UNAVAILABLE = "Room status is not AVAILABLE"
    
    // Error Messages - External Services
    const val ERR_ROOM_SERVICE_UNAVAILABLE = "Room service is unavailable"
    const val ERR_USER_SERVICE_UNAVAILABLE = "User service is unavailable"
    const val ERR_ROOM_NOT_FOUND_EXTERNAL = "Room not found in hotel management service"
    const val ERR_USER_NOT_FOUND_EXTERNAL = "User not found in user management service"
    
    // Success Messages - Order
    const val MSG_ORDER_CREATED = "Order created successfully"
    const val MSG_ORDER_UPDATED = "Order updated successfully"
    const val MSG_ORDER_DELETED = "Order deleted successfully"
    const val MSG_ORDER_RESTORED = "Order restored successfully"
    const val MSG_GET_ALL_ORDERS = "Success get all orders"
    const val MSG_GET_ORDER_BY_ID = "Success get order by id"
    const val MSG_GET_ORDERS_BY_USER = "Success get orders by user"
    
    // Success Messages - Payment
    const val MSG_PAYMENT_CREATED = "Payment created successfully. Status: PENDING (awaiting admin approval)"
    const val MSG_PAYMENT_UPDATED = "Payment updated successfully"
    const val MSG_PAYMENT_DELETED = "Payment deleted successfully"
    const val MSG_PAYMENT_RESTORED = "Payment restored successfully"
    const val MSG_GET_ALL_PAYMENTS = "Success get all payments"
    const val MSG_GET_PAYMENT_BY_ID = "Success get payment by id"
    const val MSG_GET_PAYMENTS_BY_ORDER = "Success get payments by order"
    const val MSG_GET_TOTAL_PAID = "Success get total paid amount (SUCCESS payments only)"
    
    // Error Messages - Payment
    const val ERR_PAYMENT_NOT_FOUND = "Payment not found"
    const val ERR_NO_PAYMENTS_FOUND = "No payments found"
    const val ERR_PAYMENT_AMOUNT_EXCEEDS = "Payment amount exceeds remaining amount"
    const val ERR_PAYMENT_REFERENCE_EXISTS = "Payment reference already exists"
    const val ERR_CANNOT_UPDATE_SUCCESS_PAYMENT = "Cannot update amount for SUCCESS payments"
    const val ERR_PAYMENT_ALREADY_ACTIVE = "Payment is already active"
}
package com.abuamar.order_management_service.service

import com.abuamar.order_management_service.domain.dto.res.ResOrder

interface OrderService {
    fun getAllOrders(): List<ResOrder>
    fun getOrdersByUserId(userId: Int): List<ResOrder>
}
package com.abuamar.order_management_service.service

import com.abuamar.order_management_service.domain.dto.req.ReqCreateOrder
import com.abuamar.order_management_service.domain.dto.req.ReqUpdateOrder
import com.abuamar.order_management_service.domain.dto.res.ResOrder

interface OrderService {
    fun getAllOrders(): List<ResOrder>
    fun getOrderById(id: Int): ResOrder
    fun getOrdersByUserId(userId: Int): List<ResOrder>
    fun createOrder(request: ReqCreateOrder): ResOrder
    fun updateOrder(id: Int, request: ReqUpdateOrder): ResOrder
    fun deleteOrder(id: Int)
    fun restoreOrder(id: Int): ResOrder
}
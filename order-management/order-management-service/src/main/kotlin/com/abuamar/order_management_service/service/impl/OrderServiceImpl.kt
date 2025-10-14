package com.abuamar.order_management_service.service.impl

import com.abuamar.order_management_service.domain.dto.res.ResOrder
import com.abuamar.order_management_service.exception.CustomException
import com.abuamar.order_management_service.repository.MasterOrderRepository
import com.abuamar.order_management_service.service.OrderService
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(
    private val orderRepository: MasterOrderRepository
): OrderService {
    
    override fun getAllOrders(): List<ResOrder> {
        val orders = orderRepository.findAll().ifEmpty {
            throw CustomException(
                "No orders found",
                404
            )
        }

        return orders.map { order ->
            ResOrder(
                id = order.id,
                orderNumber = order.orderNumber,
                userId = order.userId,
                roomNumber = order.room.roomNumber,
                hotelName = order.room.hotel.name,
                checkInDate = order.checkInDate,
                checkOutDate = order.checkOutDate,
                totalAmount = order.totalAmount,
                status = order.status.name,
                guestCount = order.guestCount,
                specialRequests = order.specialRequests,
                paymentStatus = order.paymentStatus.name,
                createdAt = order.createdAt,
                updatedAt = order.updatedAt
            )
        }
    }

    override fun getOrdersByUserId(userId: Int): List<ResOrder> {
        val orders = orderRepository.findAllByUserId(userId).ifEmpty {
            throw CustomException(
                "No orders found for user ID: $userId",
                404
            )
        }

        return orders.map { order ->
            ResOrder(
                id = order.id,
                orderNumber = order.orderNumber,
                userId = order.userId,
                roomNumber = order.room.roomNumber,
                hotelName = order.room.hotel.name,
                checkInDate = order.checkInDate,
                checkOutDate = order.checkOutDate,
                totalAmount = order.totalAmount,
                status = order.status.name,
                guestCount = order.guestCount,
                specialRequests = order.specialRequests,
                paymentStatus = order.paymentStatus.name,
                createdAt = order.createdAt,
                updatedAt = order.updatedAt
            )
        }
    }
}
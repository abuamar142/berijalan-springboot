package com.abuamar.order_management_service.controller

import com.abuamar.order_management_service.domain.dto.res.BaseResponse
import com.abuamar.order_management_service.domain.dto.res.ResOrder
import com.abuamar.order_management_service.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping
    fun getAllOrders(): ResponseEntity<BaseResponse<List<ResOrder>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all orders",
                data = orderService.getAllOrders(),
            )
        )
    }

    @GetMapping("/user/{userId}")
    fun getOrdersByUserId(
        @PathVariable userId: Int
    ): ResponseEntity<BaseResponse<List<ResOrder>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get orders for user ID: $userId",
                data = orderService.getOrdersByUserId(userId),
            )
        )
    }
}
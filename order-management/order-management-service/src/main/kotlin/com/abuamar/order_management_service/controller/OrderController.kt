package com.abuamar.order_management_service.controller

import com.abuamar.order_management_service.domain.dto.res.BaseResponse
import com.abuamar.order_management_service.domain.dto.req.ReqCreateOrder
import com.abuamar.order_management_service.domain.dto.req.ReqUpdateOrder
import com.abuamar.order_management_service.domain.dto.res.ResOrder
import com.abuamar.order_management_service.service.OrderService
import com.abuamar.order_management_service.util.AppConstants
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
                message = AppConstants.MSG_GET_ALL_ORDERS,
                data = orderService.getAllOrders()
            )
        )
    }

    @GetMapping("/{id}")
    fun getOrderById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResOrder>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "${AppConstants.MSG_GET_ORDER_BY_ID} $id",
                data = orderService.getOrderById(id)
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
                message = "${AppConstants.MSG_GET_ORDERS_BY_USER} $userId",
                data = orderService.getOrdersByUserId(userId)
            )
        )
    }

    @PostMapping
    fun createOrder(
        @Valid @RequestBody request: ReqCreateOrder
    ): ResponseEntity<BaseResponse<ResOrder>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_ORDER_CREATED,
                data = orderService.createOrder(request)
            )
        )
    }

    @PutMapping("/{id}")
    fun updateOrder(
        @PathVariable id: Int,
        @Valid @RequestBody request: ReqUpdateOrder
    ): ResponseEntity<BaseResponse<ResOrder>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_ORDER_UPDATED,
                data = orderService.updateOrder(id, request)
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteOrder(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<Unit>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_ORDER_DELETED,
                data = orderService.deleteOrder(id)
            )
        )
    }

    @PatchMapping("/{id}")
    fun restoreOrder(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResOrder>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_ORDER_RESTORED,
                data = orderService.restoreOrder(id)
            )
        )
    }
}
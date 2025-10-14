package com.abuamar.order_management_service.controller

import com.abuamar.order_management_service.domain.dto.req.ReqCreatePayment
import com.abuamar.order_management_service.domain.dto.req.ReqUpdatePayment
import com.abuamar.order_management_service.domain.dto.res.BaseResponse
import com.abuamar.order_management_service.domain.dto.res.ResPayment
import com.abuamar.order_management_service.service.PaymentService
import com.abuamar.order_management_service.util.AppConstants
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    @GetMapping
    fun getAllPayments(): ResponseEntity<BaseResponse<List<ResPayment>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_GET_ALL_PAYMENTS,
                data = paymentService.getAllPayments()
            )
        )
    }

    @GetMapping("/{id}")
    fun getPaymentById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResPayment>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "${AppConstants.MSG_GET_PAYMENT_BY_ID} $id",
                data = paymentService.getPaymentById(id)
            )
        )
    }

    @GetMapping("/order/{orderId}")
    fun getPaymentsByOrderId(
        @PathVariable orderId: Int
    ): ResponseEntity<BaseResponse<List<ResPayment>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "${AppConstants.MSG_GET_PAYMENTS_BY_ORDER} $orderId",
                data = paymentService.getPaymentsByOrderId(orderId)
            )
        )
    }

    @GetMapping("/order/{orderId}/total-paid")
    fun getTotalPaidAmount(
        @PathVariable orderId: Int
    ): ResponseEntity<BaseResponse<Int>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_GET_TOTAL_PAID,
                data = paymentService.getTotalPaidAmount(orderId)
            )
        )
    }

    @PostMapping
    fun createPayment(
        @Valid @RequestBody request: ReqCreatePayment
    ): ResponseEntity<BaseResponse<ResPayment>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_PAYMENT_CREATED,
                data = paymentService.createPayment(request)
            )
        )
    }

    @PutMapping("/{id}")
    fun updatePayment(
        @PathVariable id: Int,
        @Valid @RequestBody request: ReqUpdatePayment
    ): ResponseEntity<BaseResponse<ResPayment>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_PAYMENT_UPDATED,
                data = paymentService.updatePayment(id, request)
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deletePayment(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<Unit>> {
        paymentService.deletePayment(id)
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_PAYMENT_DELETED,
                data = null
            )
        )
    }

    @PatchMapping("/{id}")
    fun restorePayment(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResPayment>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = AppConstants.MSG_PAYMENT_RESTORED,
                data = paymentService.restorePayment(id)
            )
        )
    }
}

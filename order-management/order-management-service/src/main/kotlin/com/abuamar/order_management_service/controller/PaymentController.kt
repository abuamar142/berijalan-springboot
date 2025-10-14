package com.abuamar.order_management_service.controller

import com.abuamar.order_management_service.domain.dto.req.ReqCreatePayment
import com.abuamar.order_management_service.domain.dto.req.ReqUpdatePayment
import com.abuamar.order_management_service.domain.dto.res.BaseResponse
import com.abuamar.order_management_service.domain.dto.res.ResPayment
import com.abuamar.order_management_service.service.PaymentService
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
        val payments = paymentService.getAllPayments()

        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Payments retrieved successfully",
                data = payments
            )
        )
    }

    @GetMapping("/{id}")
    fun getPaymentById(@PathVariable id: Int): ResponseEntity<BaseResponse<ResPayment>> {
        val payment = paymentService.getPaymentById(id)

        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Payment retrieved successfully",
                data = payment
            )
        )
    }

    @GetMapping("/order/{orderId}")
    fun getPaymentsByOrderId(@PathVariable orderId: Int): ResponseEntity<BaseResponse<List<ResPayment>>> {
        val payments = paymentService.getPaymentsByOrderId(orderId)

        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Payments for order retrieved successfully",
                data = payments
            )
        )
    }

    @PostMapping
    fun createPayment(@Valid @RequestBody request: ReqCreatePayment): ResponseEntity<BaseResponse<ResPayment>> {
        val payment = paymentService.createPayment(request)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            BaseResponse(
                success = true,
                message = "Payment created successfully",
                data = payment
            )
        )
    }

    @PutMapping("/{id}")
    fun updatePayment(
        @PathVariable id: Int,
        @Valid @RequestBody request: ReqUpdatePayment
    ): ResponseEntity<BaseResponse<ResPayment>> {
        val payment = paymentService.updatePayment(id, request)

        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Payment updated successfully",
                data = payment
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deletePayment(@PathVariable id: Int): ResponseEntity<BaseResponse<Unit>> {
        paymentService.deletePayment(id)

        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Payment deleted successfully",
                data = Unit
            )
        )
    }

    @GetMapping("/order/{orderId}/total-paid")
    fun getTotalPaidAmount(@PathVariable orderId: Int): ResponseEntity<BaseResponse<Int>> {
        val totalPaid = paymentService.getTotalPaidAmount(orderId)

        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Total paid amount retrieved successfully",
                data = totalPaid
            )
        )
    }

    @PatchMapping("/{id}")
    fun restorePayment(@PathVariable id: Int): ResponseEntity<BaseResponse<ResPayment>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success restore payment with id $id",
                data = paymentService.restorePayment(id)
            )
        )
    }
}

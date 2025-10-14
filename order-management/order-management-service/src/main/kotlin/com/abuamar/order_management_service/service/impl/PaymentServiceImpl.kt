package com.abuamar.order_management_service.service.impl

import com.abuamar.order_management_service.domain.dto.req.ReqCreatePayment
import com.abuamar.order_management_service.domain.dto.req.ReqUpdatePayment
import com.abuamar.order_management_service.domain.dto.res.ResPayment
import com.abuamar.order_management_service.domain.entity.MasterListPaymentEntity
import com.abuamar.order_management_service.domain.enum.PaymentStatus
import com.abuamar.order_management_service.domain.enum.TransactionPaymentStatus
import com.abuamar.order_management_service.exception.CustomException
import com.abuamar.order_management_service.repository.MasterListPaymentRepository
import com.abuamar.order_management_service.repository.MasterOrderRepository
import com.abuamar.order_management_service.service.PaymentService
import com.abuamar.order_management_service.util.AppConstants
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class PaymentServiceImpl(
    private val paymentRepository: MasterListPaymentRepository,
    private val orderRepository: MasterOrderRepository,
    private val httpServletRequest: HttpServletRequest
) : PaymentService {
    private fun getAuthenticatedUserId(): String {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_ID)
            ?: throw CustomException(
                AppConstants.ERR_AUTH_HEADER_MISSING,
                HttpStatus.UNAUTHORIZED.value()
            )
    }

    private fun isAdmin(): Boolean {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_AUTHORITY) == AppConstants.ROLE_ADMIN
    }

    private fun requireAdmin() {
        if (!isAdmin()) {
            throw CustomException(
                AppConstants.ERR_UNAUTHORIZED,
                HttpStatus.FORBIDDEN.value()
            )
        }
    }

    private fun mapToResPayment(payment: MasterListPaymentEntity): ResPayment {
        // Get order number
        val order = orderRepository.findActiveById(payment.orderId).orElse(null)

        return ResPayment(
            id = payment.id,
            orderId = payment.orderId,
            orderNumber = order?.orderNumber,
            paymentMethod = payment.paymentMethod,
            paymentAmount = payment.paymentAmount,
            paymentDate = payment.paymentDate,
            transactionId = payment.transactionId,
            paymentReference = payment.paymentReference,
            paymentStatus = payment.paymentStatus,
            paymentGateway = payment.paymentGateway,
            notes = payment.notes,
            createdAt = payment.createdAt!!,
            updatedAt = payment.updatedAt!!,
            createdBy = payment.createdBy ?: AppConstants.SYSTEM_USER,
            updatedBy = payment.updatedBy ?: AppConstants.SYSTEM_USER
        )
    }

    override fun getAllPayments(): List<ResPayment> {
        val payments = paymentRepository.findAllActive()

        if (payments.isEmpty()) {
            throw CustomException(
                "No payments found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return payments.map { mapToResPayment(it) }
    }

    override fun getPaymentById(id: Int): ResPayment {
        val payment = paymentRepository.findActiveById(id)
            ?: throw CustomException(
                "Payment not found with id $id",
                HttpStatus.NOT_FOUND.value()
            )

        return mapToResPayment(payment)
    }

    override fun getPaymentsByOrderId(orderId: Int): List<ResPayment> {
        // Validate order exists
        orderRepository.findActiveById(orderId).orElseThrow {
            CustomException(
                "Order not found with id $orderId",
                HttpStatus.NOT_FOUND.value()
            )
        }

        val payments = paymentRepository.findByOrderId(orderId)

        if (payments.isEmpty()) {
            throw CustomException(
                "No payments found for order ID: $orderId",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return payments.map { mapToResPayment(it) }
    }

    @Transactional
    override fun createPayment(request: ReqCreatePayment): ResPayment {
        val userId = getAuthenticatedUserId()

        // Validate order exists
        val order = orderRepository.findActiveById(request.orderId).orElseThrow {
            CustomException(
                "Order not found with id ${request.orderId}",
                HttpStatus.NOT_FOUND.value()
            )
        }

        // Calculate remaining based on SUCCESS payments only
        val totalSuccessPaid = paymentRepository.getTotalSuccessPayments(request.orderId)
        val remaining = order.totalAmount - totalSuccessPaid

        // Validate payment amount doesn't exceed remaining
        if (request.paymentAmount > remaining) {
            throw CustomException(
                "Payment amount (${request.paymentAmount}) exceeds remaining amount ($remaining). " +
                "Total: ${order.totalAmount}, Paid (SUCCESS): $totalSuccessPaid",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        // Check for duplicate payment reference
        if (request.paymentReference != null) {
            val existing = paymentRepository.findByTransactionId(request.paymentReference)
            if (existing != null) {
                throw CustomException(
                    "Payment with reference ${request.paymentReference} already exists",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
        }

        // Create entity
        val payment = MasterListPaymentEntity(
            orderId = request.orderId,
            paymentMethod = request.paymentMethod,
            paymentAmount = request.paymentAmount,
            paymentDate = request.paymentDate ?: Timestamp(System.currentTimeMillis()), // Default to now
            transactionId = request.transactionId,
            paymentReference = request.paymentReference,
            paymentStatus = TransactionPaymentStatus.PENDING, // Always PENDING on create
            paymentGateway = request.paymentGateway,
            notes = request.notes
        )

        payment.createdBy = userId.toString()
        payment.updatedBy = userId.toString()
        payment.createdAt = Timestamp(System.currentTimeMillis())
        payment.updatedAt = Timestamp(System.currentTimeMillis())
        payment.isActive = true
        payment.isDelete = false

        val savedPayment = paymentRepository.save(payment)

        // Don't update order payment status yet (payment is PENDING)
        // Admin must approve (set to SUCCESS) before it counts

        return mapToResPayment(savedPayment)
    }

    @Transactional
    override fun updatePayment(id: Int, request: ReqUpdatePayment): ResPayment {
        val userId = getAuthenticatedUserId()

        val payment = paymentRepository.findActiveById(id)
            ?: throw CustomException(
                "Payment not found with id $id",
                HttpStatus.NOT_FOUND.value()
            )

        // Prevent amount update if payment is already SUCCESS
        if (request.paymentAmount != null && payment.paymentStatus == TransactionPaymentStatus.SUCCESS) {
            throw CustomException(
                "Cannot update payment amount for payments with SUCCESS status",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        // Update fields
        request.paymentMethod?.let { payment.paymentMethod = it }
        request.paymentDate?.let { payment.paymentDate = it }
        request.paymentReference?.let { payment.paymentReference = it }
        request.paymentGateway?.let { payment.paymentGateway = it }
        request.notes?.let { payment.notes = it }
        
        // Update payment status (admin can change PENDING → SUCCESS/FAILED)
        request.paymentStatus?.let { payment.paymentStatus = it }

        // Update amount if allowed
        if (request.paymentAmount != null) {
            val order = orderRepository.findActiveById(payment.orderId).orElseThrow {
                CustomException(
                    "Order not found with id ${payment.orderId}",
                    HttpStatus.NOT_FOUND.value()
                )
            }

            // Calculate remaining (exclude current payment if SUCCESS, only count other SUCCESS payments)
            val totalSuccessPaid = paymentRepository.getTotalSuccessPayments(payment.orderId)
            val currentSuccessAmount = if (payment.paymentStatus == TransactionPaymentStatus.SUCCESS) payment.paymentAmount else 0
            val remaining = order.totalAmount - (totalSuccessPaid - currentSuccessAmount)

            if (request.paymentAmount > remaining) {
                throw CustomException(
                    "Payment amount (${request.paymentAmount}) exceeds remaining amount ($remaining)",
                    HttpStatus.BAD_REQUEST.value()
                )
            }

            payment.paymentAmount = request.paymentAmount
        }

        payment.updatedBy = userId
        payment.updatedAt = Timestamp(System.currentTimeMillis())

        val updatedPayment = paymentRepository.save(payment)

        // Update order payment status if status or amount changed
        if (request.paymentStatus != null || request.paymentAmount != null) {
            updateOrderPaymentStatus(payment.orderId)
        }

        return mapToResPayment(updatedPayment)
    }

    @Transactional
    override fun deletePayment(id: Int) {
        requireAdmin()

        val userId = getAuthenticatedUserId()

        val payment = paymentRepository.findActiveById(id)
            ?: throw CustomException(
                "Payment not found with id $id",
                HttpStatus.NOT_FOUND.value()
            )

        payment.isDelete = true
        payment.deletedAt = Timestamp(System.currentTimeMillis())
        payment.deletedBy = userId

        paymentRepository.save(payment)

        // Recalculate order payment status (exclude deleted payment)
        updateOrderPaymentStatus(payment.orderId)
    }

    override fun getTotalPaidAmount(orderId: Int): Int {
        // Validate order exists
        orderRepository.findActiveById(orderId).orElseThrow {
            CustomException(
                "Order not found with id $orderId",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        // Only return total SUCCESS payments
        return paymentRepository.getTotalSuccessPayments(orderId)
    }

    // Update order payment status based on total SUCCESS payments only
    private fun updateOrderPaymentStatus(orderId: Int) {
        val order = orderRepository.findActiveById(orderId).orElse(null) ?: return
        
        // Only count SUCCESS payments
        val totalSuccessPaid = paymentRepository.getTotalSuccessPayments(orderId)
        
        val newStatus = when {
            totalSuccessPaid == 0 -> PaymentStatus.UNPAID
            totalSuccessPaid < order.totalAmount -> PaymentStatus.PARTIAL
            totalSuccessPaid >= order.totalAmount -> PaymentStatus.PAID
            else -> order.paymentStatus
        }
        
        if (order.paymentStatus != newStatus) {
            order.paymentStatus = newStatus
            order.updatedBy = getAuthenticatedUserId().toString()
            order.updatedAt = Timestamp(System.currentTimeMillis())
            orderRepository.save(order)
        }
    }

    @Transactional
    override fun restorePayment(id: Int): ResPayment {
        requireAdmin()

        val userId = getAuthenticatedUserId()

        val payment = paymentRepository.findById(id).orElseThrow {
            CustomException(
                "Payment not found with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (!payment.isDelete) {
            throw CustomException(
                "Payment with id $id is already active",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        payment.isDelete = false
        payment.deletedAt = null
        payment.deletedBy = null
        payment.updatedBy = userId
        payment.updatedAt = Timestamp(System.currentTimeMillis())

        val restoredPayment = paymentRepository.save(payment)

        // Recalculate order payment status (include restored payment if SUCCESS)
        updateOrderPaymentStatus(payment.orderId)

        return mapToResPayment(restoredPayment)
    }
}

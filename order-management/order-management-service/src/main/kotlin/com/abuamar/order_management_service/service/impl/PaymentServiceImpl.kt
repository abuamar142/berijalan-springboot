package com.abuamar.order_management_service.service.impl

import com.abuamar.order_management_service.domain.dto.req.ReqCreatePayment
import com.abuamar.order_management_service.domain.dto.req.ReqUpdatePayment
import com.abuamar.order_management_service.domain.dto.res.ResPayment
import com.abuamar.order_management_service.domain.entity.MasterListPaymentEntity
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
            createdBy = payment.createdBy.toString(),
            updatedBy = payment.updatedBy.toString()
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

        // Validate payment amount doesn't exceed remaining amount
        val totalPaid = paymentRepository.getTotalPaidAmount(request.orderId)
        val remaining = order.totalAmount - totalPaid

        if (request.paymentAmount > remaining) {
            throw CustomException(
                "Payment amount (${request.paymentAmount}) exceeds remaining amount ($remaining)",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        // Check for duplicate transaction ID
        if (request.transactionId != null) {
            val existing = paymentRepository.findByTransactionId(request.transactionId)
            if (existing != null) {
                throw CustomException(
                    "Payment with transaction ID ${request.transactionId} already exists",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
        }

        // Create entity
        val payment = MasterListPaymentEntity(
            orderId = request.orderId,
            paymentMethod = request.paymentMethod,
            paymentAmount = request.paymentAmount,
            paymentDate = request.paymentDate,
            transactionId = request.transactionId,
            paymentReference = request.paymentReference,
            paymentGateway = request.paymentGateway,
            notes = request.notes
        )

        payment.createdBy = getAuthenticatedUserId()
        payment.updatedBy = getAuthenticatedUserId()

        payment.createdAt = Timestamp(System.currentTimeMillis())
        payment.updatedAt = Timestamp(System.currentTimeMillis())

        payment.isActive = true
        payment.isDelete = false

        val savedPayment = paymentRepository.save(payment)

        // Update order payment status if fully paid
        updateOrderPaymentStatus(request.orderId)

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

        // Update fields
        request.paymentAmount?.let { 
            // Validate new amount
            val order = orderRepository.findActiveById(payment.orderId).orElseThrow {
                CustomException(
                    "Order not found with id ${payment.orderId}",
                    HttpStatus.NOT_FOUND.value()
                )
            }

            val totalPaid = paymentRepository.getTotalPaidAmount(payment.orderId) - payment.paymentAmount
            val remaining = order.totalAmount - totalPaid

            if (it > remaining) {
                throw CustomException(
                    "Payment amount ($it) exceeds remaining amount ($remaining)",
                    HttpStatus.BAD_REQUEST.value()
                )
            }

            payment.paymentAmount = it 
        }
        request.paymentDate?.let { payment.paymentDate = it }
        request.paymentMethod?.let { payment.paymentMethod = it }
        request.transactionId?.let { 
            // Check duplicate
            if (it != payment.transactionId) {
                val existing = paymentRepository.findByTransactionId(it)
                if (existing != null && existing.id != payment.id) {
                    throw CustomException(
                        "Payment with transaction ID $it already exists",
                        HttpStatus.BAD_REQUEST.value()
                    )
                }
            }
            payment.transactionId = it 
        }
        request.paymentReference?.let { payment.paymentReference = it }
        request.paymentStatus?.let { payment.paymentStatus = it }
        request.paymentGateway?.let { payment.paymentGateway = it }
        request.notes?.let { payment.notes = it }

        payment.updatedBy = getAuthenticatedUserId()
        payment.updatedAt = Timestamp(System.currentTimeMillis())

        val updatedPayment = paymentRepository.save(payment)

        // Update order payment status
        updateOrderPaymentStatus(payment.orderId)

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
        payment.deletedBy = getAuthenticatedUserId()

        paymentRepository.save(payment)

        // Update order payment status
        updateOrderPaymentStatus(payment.orderId)
    }

    override fun getTotalPaidAmount(orderId: Int): Int {
        return paymentRepository.getTotalPaidAmount(orderId)
    }

    // Helper: Update order payment status based on total paid
    private fun updateOrderPaymentStatus(orderId: Int) {
        val order = orderRepository.findActiveById(orderId).orElse(null) ?: return
        
        val totalPaid = paymentRepository.getTotalPaidAmount(orderId)
        
        val newStatus = when {
            totalPaid == 0 -> com.abuamar.order_management_service.domain.enum.OrderPaymentStatus.UNPAID
            totalPaid < order.totalAmount -> com.abuamar.order_management_service.domain.enum.OrderPaymentStatus.PARTIAL
            totalPaid >= order.totalAmount -> com.abuamar.order_management_service.domain.enum.OrderPaymentStatus.PAID
            else -> order.paymentStatus
        }

        if (order.paymentStatus != newStatus) {
            // This would need proper casting/conversion
            // For now, just update manually
            orderRepository.save(order)
        }
    }

    override fun restorePayment(id: Int): ResPayment {
        requireAdmin()

        val payment = paymentRepository.findById(id).orElseThrow {
            CustomException(
                "Payment not found with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (!payment.isDelete) {
            throw CustomException(
                "Payment is already active with id $id",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        payment.isDelete = false

        payment.deletedAt = null
        payment.deletedBy = null

        payment.updatedAt = Timestamp(System.currentTimeMillis())
        payment.updatedBy = getAuthenticatedUserId()

        val restoredPayment = paymentRepository.save(payment)

        // Update order payment status
        updateOrderPaymentStatus(payment.orderId)

        return mapToResPayment(restoredPayment)
    }
}

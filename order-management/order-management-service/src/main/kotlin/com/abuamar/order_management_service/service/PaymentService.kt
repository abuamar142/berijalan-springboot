package com.abuamar.order_management_service.service

import com.abuamar.order_management_service.domain.dto.req.ReqCreatePayment
import com.abuamar.order_management_service.domain.dto.req.ReqUpdatePayment
import com.abuamar.order_management_service.domain.dto.res.ResPayment

interface PaymentService {
    fun getAllPayments(): List<ResPayment>
    fun getPaymentById(id: Int): ResPayment
    fun getPaymentsByOrderId(orderId: Int): List<ResPayment>
    fun createPayment(request: ReqCreatePayment): ResPayment
    fun updatePayment(id: Int, request: ReqUpdatePayment): ResPayment
    fun deletePayment(id: Int)
    fun getTotalPaidAmount(orderId: Int): Int
    fun restorePayment(id: Int): ResPayment
}

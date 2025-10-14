package com.abuamar.order_management_service.domain.entity

import com.abuamar.order_management_service.domain.enum.PaymentMethod
import com.abuamar.order_management_service.domain.enum.TransactionPaymentStatus
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "trn_list_payment")
data class MasterListPaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "order_id", nullable = false)
    var orderId: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    var paymentMethod: PaymentMethod,

    @Column(name = "payment_amount", nullable = false)
    var paymentAmount: Int,

    @Column(name = "payment_date", nullable = false)
    var paymentDate: Timestamp,

    @Column(name = "transaction_id", nullable = true, length = 100, unique = true)
    var transactionId: String? = null,

    @Column(name = "payment_reference", nullable = true, length = 100)
    var paymentReference: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    var paymentStatus: TransactionPaymentStatus = TransactionPaymentStatus.PENDING,

    @Column(name = "payment_gateway", nullable = true, length = 50)
    var paymentGateway: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null
) : BaseEntity()
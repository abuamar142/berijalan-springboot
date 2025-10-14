package com.abuamar.order_management_service.domain.entitiy

import jakarta.persistence.*
import java.math.BigDecimal
import java.sql.Timestamp

@Entity
@Table(name = "mst_list_payment")
data class MasterListPaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: MasterOrderEntity,

    @Column(name = "payment_method", nullable = false)
    val paymentMethod: String, // CREDIT_CARD, DEBIT_CARD, CASH, BANK_TRANSFER, E_WALLET

    @Column(name = "payment_amount", nullable = false, precision = 19, scale = 2)
    val paymentAmount: BigDecimal,

    @Column(name = "payment_date", nullable = false)
    val paymentDate: Timestamp,

    @Column(name = "transaction_id", nullable = true)
    val transactionId: String? = null,

    @Column(name = "payment_reference", nullable = true)
    val paymentReference: String? = null,

    @Column(name = "payment_status", nullable = false)
    val paymentStatus: String = "PENDING", // PENDING, SUCCESS, FAILED, CANCELLED

    @Column(name = "payment_gateway", nullable = true)
    val paymentGateway: String? = null, // MIDTRANS, XENDIT, GOPAY, OVO, etc.

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: Timestamp? = null,
)
package com.abuamar.order_management_service.domain.entitiy

import jakarta.persistence.*
import java.math.BigDecimal
import java.sql.Timestamp
import com.abuamar.order_management_service.domain.enum.OrderStatus
import com.abuamar.order_management_service.domain.enum.PaymentStatus

@Entity
@Table(name = "mst_order")
data class MasterOrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "order_number", nullable = false, unique = true)
    val orderNumber: String,

    @Column(name = "user_id", nullable = false)
    val userId: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: MasterRoomEntity,

    @Column(name = "check_in_date", nullable = false)
    val checkInDate: Timestamp,

    @Column(name = "check_out_date", nullable = false)
    val checkOutDate: Timestamp,

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    val totalAmount: BigDecimal,

    @Column(name = "status", nullable = false)
    val status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "guest_count", nullable = false)
    val guestCount: Int = 1,

    @Column(name = "special_requests", columnDefinition = "TEXT")
    val specialRequests: String? = null,

    @Column(name = "payment_status", nullable = false)
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: Timestamp? = null,
)
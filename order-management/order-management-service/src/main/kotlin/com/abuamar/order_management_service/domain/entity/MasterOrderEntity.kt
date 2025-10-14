package com.abuamar.order_management_service.domain.entity

import jakarta.persistence.*
import java.sql.Date
import com.abuamar.order_management_service.domain.enum.OrderStatus
import com.abuamar.order_management_service.domain.enum.PaymentStatus

@Entity
@Table(name = "trn_order")
data class MasterOrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    var orderNumber: String,

    @Column(name = "user_id", nullable = false)
    var userId: Int,

    @Column(name = "room_id", nullable = false)
    var roomId: Int,

    @Column(name = "check_in_date", nullable = false)
    var checkInDate: Date,

    @Column(name = "check_out_date", nullable = false)
    var checkOutDate: Date,

    @Column(name = "nights", nullable = false)
    var nights: Int,

    @Column(name = "guest_count", nullable = false)
    var guestCount: Int,

    @Column(name = "total_amount", nullable = false)
    var totalAmount: Int,

    @Column(name = "special_requests", columnDefinition = "TEXT")
    var specialRequests: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: OrderStatus = OrderStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    var paymentStatus: PaymentStatus = PaymentStatus.UNPAID
) : BaseEntity()
package com.abuamar.order_management_service.repository

import com.abuamar.order_management_service.domain.entity.MasterOrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.sql.Date
import java.util.Optional

interface MasterOrderRepository : JpaRepository<MasterOrderEntity, Int> {
    @Query(
        """
        SELECT * FROM trn_order 
        WHERE is_delete = false 
        ORDER BY created_at DESC
        """,
        nativeQuery = true
    )
    fun findAllActive(): List<MasterOrderEntity>

    @Query(
        """
        SELECT * FROM trn_order 
        WHERE id = :id 
        AND is_delete = false
        """,
        nativeQuery = true
    )
    fun findActiveById(id: Int): Optional<MasterOrderEntity>

    @Query(
        """
        SELECT * FROM trn_order 
        WHERE order_number = :orderNumber 
        AND is_delete = false
        """,
        nativeQuery = true
    )
    fun findByOrderNumber(@Param("orderNumber") orderNumber: String): MasterOrderEntity?

    @Query(
        """
        SELECT * FROM trn_order 
        WHERE user_id = :userId 
        AND is_delete = false 
        ORDER BY created_at DESC
        """,
        nativeQuery = true
    )
    fun findByUserId(@Param("userId") userId: Int): List<MasterOrderEntity>

    @Query(
        """
        SELECT * FROM trn_order 
        WHERE room_id = :roomId 
        AND is_delete = false 
        AND status IN ('PENDING', 'CONFIRMED')
        AND (
            (check_in_date <= :checkOutDate AND check_out_date >= :checkInDate)
        )
        """,
        nativeQuery = true
    )
    fun findConflictingOrders(
        @Param("roomId") roomId: Int,
        @Param("checkInDate") checkInDate: Date,
        @Param("checkOutDate") checkOutDate: Date
    ): List<MasterOrderEntity>

    @Query(
        """
        SELECT COUNT(*) FROM trn_order 
        WHERE is_delete = false 
        AND DATE(created_at) = CURRENT_DATE
        """,
        nativeQuery = true
    )
    fun countOrdersToday(): Int
}
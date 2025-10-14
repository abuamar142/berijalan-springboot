package com.abuamar.order_management_service.repository

import com.abuamar.order_management_service.domain.entity.MasterListPaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MasterListPaymentRepository : JpaRepository<MasterListPaymentEntity, Int> {

    @Query(
        """
        SELECT * FROM trn_list_payment 
        WHERE is_delete = false 
        ORDER BY payment_date DESC
        """,
        nativeQuery = true
    )
    fun findAllActive(): List<MasterListPaymentEntity>

    @Query(
        """
        SELECT * FROM trn_list_payment 
        WHERE id = :id 
        AND is_delete = false
        """,
        nativeQuery = true
    )
    fun findActiveById(@Param("id") id: Int): MasterListPaymentEntity?

    @Query(
        """
        SELECT * FROM trn_list_payment 
        WHERE order_id = :orderId 
        AND is_delete = false 
        ORDER BY payment_date DESC
        """,
        nativeQuery = true
    )
    fun findByOrderId(@Param("orderId") orderId: Int): List<MasterListPaymentEntity>

    @Query(
        """
        SELECT * FROM trn_list_payment 
        WHERE transaction_id = :transactionId 
        AND is_delete = false
        """,
        nativeQuery = true
    )
    fun findByTransactionId(@Param("transactionId") transactionId: String): MasterListPaymentEntity?

    @Query(
        """
        SELECT COALESCE(SUM(payment_amount), 0) FROM trn_list_payment 
        WHERE order_id = :orderId 
        AND payment_status = 'SUCCESS' 
        AND is_delete = false
        """,
        nativeQuery = true
    )
    fun getTotalPaidAmount(@Param("orderId") orderId: Int): Int
}

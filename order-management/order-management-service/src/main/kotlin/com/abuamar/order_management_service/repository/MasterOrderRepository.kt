package com.abuamar.order_management_service.repository

import com.abuamar.order_management_service.domain.entitiy.MasterOrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MasterOrderRepository: JpaRepository<MasterOrderEntity, Int> {
    @Query(
        """
            SELECT o FROM MasterOrderEntity o 
            JOIN FETCH o.room r 
            JOIN FETCH r.hotel h
            WHERE o.deletedAt IS NULL
            ORDER BY o.createdAt DESC
        """
    )
    override fun findAll(): List<MasterOrderEntity>

    @Query(
        """
            SELECT o FROM MasterOrderEntity o 
            JOIN FETCH o.room r 
            JOIN FETCH r.hotel h
            WHERE o.userId = :userId 
            AND o.deletedAt IS NULL
            ORDER BY o.createdAt DESC
        """
    )
    fun findAllByUserId(userId: Int): List<MasterOrderEntity>
}
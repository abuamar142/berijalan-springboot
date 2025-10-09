package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.dto.req.ReqProduct
import com.abuamar.hotel_management_service.domain.entitiy.MasterProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MasterProductRepository: JpaRepository<MasterProductEntity, Int> {
    @Query(
        """
            SELECT * FROM mst_product
            WHERE is_delete = false
        """,
        nativeQuery = true
    )
    override fun findAll(): List<MasterProductEntity>
}
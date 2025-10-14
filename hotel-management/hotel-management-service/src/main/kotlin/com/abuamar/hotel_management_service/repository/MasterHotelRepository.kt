package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entity.MasterHotelEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface MasterHotelRepository: JpaRepository<MasterHotelEntity, Int> {
    @Query(
        """
        SELECT * FROM mst_hotel
        WHERE is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    override fun findAll(): List<MasterHotelEntity>
    
    @Query(
        """
        SELECT * FROM mst_hotel
        WHERE id = :id
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findHotelActiveById(id: Int): Optional<MasterHotelEntity>

    @Query(
        """
        SELECT * FROM mst_hotel
        WHERE LOWER(name) = LOWER(:name)
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findByName(name: String): Optional<MasterHotelEntity>
}
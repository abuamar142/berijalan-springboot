package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entity.MasterAmenityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface MasterAmenityRepository: JpaRepository<MasterAmenityEntity, Int> {
    @Query(
        """
        SELECT * FROM mst_amenity
        WHERE is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    override fun findAll(): List<MasterAmenityEntity>
    
    @Query(
        """
        SELECT * FROM mst_amenity
        WHERE id = :id
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findAmenityActiveById(id: Int): Optional<MasterAmenityEntity>

    @Query(
        """
        SELECT * FROM mst_amenity
        WHERE name = :name
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findByName(name: String): Optional<MasterAmenityEntity>
}
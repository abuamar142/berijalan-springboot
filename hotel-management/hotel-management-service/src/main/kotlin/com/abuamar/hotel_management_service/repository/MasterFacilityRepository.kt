package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entitiy.MasterFacilityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface MasterFacilityRepository: JpaRepository<MasterFacilityEntity, Int> {
    @Query(
        """
        SELECT * FROM mst_facility
        WHERE is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    override fun findAll(): List<MasterFacilityEntity>
    
    @Query(
        """
        SELECT * FROM mst_facility
        WHERE id = :id
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findFacilityActiveById(id: Int): Optional<MasterFacilityEntity>

    @Query(
        """
        SELECT * FROM mst_facility
        WHERE name = :name
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findByName(name: String): Optional<MasterFacilityEntity>
}
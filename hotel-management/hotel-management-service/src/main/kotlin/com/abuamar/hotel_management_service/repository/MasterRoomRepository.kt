package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entity.MasterRoomEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface MasterRoomRepository: JpaRepository<MasterRoomEntity, Int> {
    @Query(
        """
        SELECT * FROM mst_room
        WHERE is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    override fun findAll(): List<MasterRoomEntity>
    
    @Query(
        """
        SELECT * FROM mst_room
        WHERE id = :id
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findRoomActiveById(id: Int): Optional<MasterRoomEntity>

    @Query(
        """
        SELECT * FROM mst_room
        WHERE LOWER(room_number) = LOWER(:roomNumber)
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findByRoomNumber(roomNumber: String): Optional<MasterRoomEntity>

    @Query(
        """
        SELECT * FROM mst_room
        WHERE is_delete = false
        AND is_active = true
        AND status = 'AVAILABLE'
        """,
        nativeQuery = true
    )
    fun findAvailableRooms(): List<MasterRoomEntity>

    @Query(
        """
        SELECT * FROM mst_room
        WHERE is_delete = false
        AND is_active = true
        AND status = :status
        ORDER BY room_number
        """,
        nativeQuery = true
    )
    fun findByStatusAndIsDeleteFalse(status: String): List<MasterRoomEntity>
}
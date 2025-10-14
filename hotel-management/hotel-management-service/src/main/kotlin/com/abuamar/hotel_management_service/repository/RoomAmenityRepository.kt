package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entity.RoomAmenityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface RoomAmenityRepository : JpaRepository<RoomAmenityEntity, Int> {
    @Modifying
    @Query(
        """
        DELETE FROM trn_room_amenity 
        WHERE room_id = :roomId
        """,
        nativeQuery = true
    )
    fun deleteByRoomId(roomId: Int)
    
    @Query(
        """
        SELECT * FROM trn_room_amenity 
        WHERE room_id = :roomId
        """,
        nativeQuery = true
    )
    fun findByRoomId(roomId: Int): List<RoomAmenityEntity>
}

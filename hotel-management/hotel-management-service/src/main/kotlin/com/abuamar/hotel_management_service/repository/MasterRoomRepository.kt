package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entitiy.MasterRoomEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MasterRoomRepository: JpaRepository<MasterRoomEntity, Int> {
}
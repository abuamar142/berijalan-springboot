package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entitiy.MasterHotelEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MasterHotelRepository: JpaRepository<MasterHotelEntity, Int> {
}
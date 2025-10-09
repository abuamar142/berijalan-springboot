package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entitiy.MasterAmenityEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MasterAmenityRepository: JpaRepository<MasterAmenityEntity, Int> {
}
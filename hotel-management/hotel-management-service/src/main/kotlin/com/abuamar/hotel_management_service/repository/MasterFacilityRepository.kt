package com.abuamar.hotel_management_service.repository

import com.abuamar.hotel_management_service.domain.entitiy.MasterFacilityEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MasterFacilityRepository: JpaRepository<MasterFacilityEntity, Int> {
}
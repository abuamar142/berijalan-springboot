package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.dto.res.ResFacility
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterFacilityRepository
import com.abuamar.hotel_management_service.service.FacilityService
import org.springframework.stereotype.Service

@Service
class FacilityServiceImpl(
    val facilityRepository: MasterFacilityRepository
): FacilityService {
    override fun getFacilities(): List<ResFacility> {
        val rawData = facilityRepository.findAll().ifEmpty {
            throw CustomException(
                "No facilities found",
                404
            )
        }

        return rawData.map {
            ResFacility(
                id = it.id,
                name = it.name,
                description = it.description ?: "",
            )
        }
    }

    override fun getFacilityById(id: Int): ResFacility? {
        val data = facilityRepository.findById(id).orElseThrow {
            throw CustomException(
                "Facility with id $id not found",
                404
            )
        }

        return ResFacility(
            id = data.id,
            name = data.name,
            description = data.description ?: "",
        )
    }
}
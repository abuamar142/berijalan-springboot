package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterAmenityRepository
import com.abuamar.hotel_management_service.service.AmenityService
import org.springframework.stereotype.Service

@Service
class AmenityServiceImpl(
    val amenityRepository: MasterAmenityRepository
): AmenityService {
    override fun getAmenities(): List<ResAmenity> {
        val rawData = amenityRepository.findAll().ifEmpty {
            throw CustomException(
                "No amenities found",
                404
            )
        }

        return rawData.map {
            ResAmenity(
                id = it.id,
                name = it.name,
                description = it.description ?: "",
            )
        }
    }

    override fun getAmenityById(id: Int): ResAmenity? {
        val data = amenityRepository.findById(id).orElseThrow {
            throw CustomException(
                "Amenity with id $id not found",
                404
            )
        }

        return ResAmenity(
            id = data.id,
            name = data.name,
            description = data.description ?: "",
        )
    }
}
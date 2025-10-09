package com.abuamar.hotel_management_service.service

import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity

interface AmenityService {
    fun getAmenities(): List<ResAmenity>
    fun getAmenityById(id: Int): ResAmenity?
}
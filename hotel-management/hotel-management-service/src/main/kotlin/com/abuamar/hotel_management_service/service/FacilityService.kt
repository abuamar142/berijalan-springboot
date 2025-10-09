package com.abuamar.hotel_management_service.service

import com.abuamar.hotel_management_service.domain.dto.res.ResFacility

interface FacilityService {
    fun getFacilities(): List<ResFacility>
    fun getFacilityById(id: Int): ResFacility?
}
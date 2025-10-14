package com.abuamar.hotel_management_service.service

import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateAmenity
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateAmenity
import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity

interface AmenityService {
    fun getAllAmenities(): List<ResAmenity>
    fun getAmenityById(id: Int): ResAmenity
    fun createAmenity(req: ReqCreateAmenity): ResAmenity
    fun updateAmenity(req: ReqUpdateAmenity): ResAmenity
    fun deleteAmenity(id: Int)
    fun restoreAmenity(id: Int): ResAmenity
}
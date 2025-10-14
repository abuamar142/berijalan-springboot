package com.abuamar.hotel_management_service.service

import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateFacility
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateFacility
import com.abuamar.hotel_management_service.domain.dto.res.ResFacility

interface FacilityService {
    fun getAllFacilities(): List<ResFacility>
    fun getFacilityById(id: Int): ResFacility
    fun createFacility(req: ReqCreateFacility): ResFacility
    fun updateFacility(req: ReqUpdateFacility): ResFacility
    fun deleteFacility(id: Int)
    fun restoreFacility(id: Int): ResFacility
}
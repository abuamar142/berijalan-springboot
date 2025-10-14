package com.abuamar.hotel_management_service.service

import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateHotel
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateHotel
import com.abuamar.hotel_management_service.domain.dto.res.ResHotel

interface HotelService {
    fun getAllHotels(): List<ResHotel>
    fun getHotelById(id: Int): ResHotel
    fun createHotel(req: ReqCreateHotel): ResHotel
    fun updateHotel(req: ReqUpdateHotel): ResHotel
    fun deleteHotel(id: Int)
    fun restoreHotel(id: Int): ResHotel
}

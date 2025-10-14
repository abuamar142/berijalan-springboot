package com.abuamar.hotel_management_service.service

import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateRoom
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateRoom
import com.abuamar.hotel_management_service.domain.dto.res.ResRoom

interface RoomService {
    fun getAllRooms(): List<ResRoom>
    fun getRoomById(id: Int): ResRoom
    fun getRoomsByHotelId(hotelId: Int): List<ResRoom>
    fun createRoom(req: ReqCreateRoom): ResRoom
    fun updateRoom(req: ReqUpdateRoom): ResRoom
    fun deleteRoom(id: Int)
    fun restoreRoom(id: Int): ResRoom
}

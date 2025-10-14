package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.constant.AppConstants
import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateRoom
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateRoom
import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity
import com.abuamar.hotel_management_service.domain.dto.res.ResHotelSimple
import com.abuamar.hotel_management_service.domain.dto.res.ResRoom
import com.abuamar.hotel_management_service.domain.entitiy.MasterRoomEntity
import com.abuamar.hotel_management_service.domain.enum.RoomStatus
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterAmenityRepository
import com.abuamar.hotel_management_service.repository.MasterHotelRepository
import com.abuamar.hotel_management_service.repository.MasterRoomRepository
import com.abuamar.hotel_management_service.service.RoomService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class RoomServiceImpl(
    private val roomRepository: MasterRoomRepository,
    private val hotelRepository: MasterHotelRepository,
    private val amenityRepository: MasterAmenityRepository,
    private val httpServletRequest: HttpServletRequest
): RoomService {
    
    // Helper Functions
    private fun getAuthenticatedUserId(): String {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_ID)
            ?: throw CustomException(
                AppConstants.ERR_AUTH_HEADER_MISSING,
                HttpStatus.UNAUTHORIZED.value()
            )
    }
    
    private fun isAdmin(): Boolean {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_AUTHORITY) == AppConstants.ROLE_ADMIN
    }
    
    private fun requireAdmin() {
        if (!isAdmin()) {
            throw CustomException(
                AppConstants.ERR_UNAUTHORIZED,
                HttpStatus.FORBIDDEN.value()
            )
        }
    }
    
    private fun mapToResRoom(room: MasterRoomEntity): ResRoom {
        return ResRoom(
            id = room.id,
            roomNumber = room.roomNumber,
            type = room.type,
            price = room.price,
            status = room.status.name,
            hotel = ResHotelSimple(
                id = room.hotel.id,
                name = room.hotel.name
            ),
            amenities = room.amenities.map { amenity ->
                ResAmenity(
                    id = amenity.id,
                    name = amenity.name,
                    description = amenity.description ?: ""
                )
            }
        )
    }
    
    override fun getAllRooms(): List<ResRoom> {
        val rooms = roomRepository.findAll()
        
        if (rooms.isEmpty()) {
            throw CustomException(
                AppConstants.ERR_NO_ROOMS_FOUND,
                HttpStatus.NOT_FOUND.value()
            )
        }

        return rooms.map { mapToResRoom(it) }
    }

    override fun getRoomById(id: Int): ResRoom {
        val room = roomRepository.findRoomActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ROOM_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return mapToResRoom(room)
    }
    
    override fun getRoomsByHotelId(hotelId: Int): List<ResRoom> {
        // Validate hotel exists
        hotelRepository.findHotelActiveById(hotelId).orElseThrow {
            throw CustomException(
                "${AppConstants.ERR_HOTEL_NOT_FOUND} with id $hotelId",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val rooms = roomRepository.findByHotelId(hotelId)
        
        if (rooms.isEmpty()) {
            throw CustomException(
                "${AppConstants.ERR_NO_ROOMS_FOUND} for hotel id $hotelId",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return rooms.map { mapToResRoom(it) }
    }
    
    @Transactional
    override fun createRoom(req: ReqCreateRoom): ResRoom {
        requireAdmin()
        
        // Validate hotel exists
        val hotel = hotelRepository.findHotelActiveById(req.hotelId).orElseThrow {
            throw CustomException(
                "${AppConstants.ERR_HOTEL_NOT_FOUND} with id ${req.hotelId}",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        // Check if room number already exists in the same hotel
        val existingRoom = roomRepository.findByRoomNumberAndHotelId(req.roomNumber, req.hotelId).orElse(null)
        
        if (existingRoom != null) {
            throw CustomException(
                "${AppConstants.ERR_ROOM_ALREADY_EXISTS} in hotel ${hotel.name}",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        val room = MasterRoomEntity(
            roomNumber = req.roomNumber,
            type = req.type,
            price = req.price,
            status = RoomStatus.valueOf(req.status ?: "AVAILABLE"),
            hotel = hotel
        )
        
        // Handle amenities if provided
        if (!req.amenityIds.isNullOrEmpty()) {
            val amenities = amenityRepository.findAllById(req.amenityIds)
            
            if (amenities.size != req.amenityIds.size) {
                throw CustomException(
                    "One or more amenity IDs are invalid",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            
            room.amenities = amenities.toMutableSet()
        }
        
        room.createdBy = userId
        room.updatedAt = Timestamp(System.currentTimeMillis())
        room.updatedBy = userId
        room.isActive = true
        room.isDelete = false
        
        val savedRoom = roomRepository.save(room)
        
        return mapToResRoom(savedRoom)
    }
    
    @Transactional
    override fun updateRoom(req: ReqUpdateRoom): ResRoom {
        requireAdmin()
        
        val room = roomRepository.findRoomActiveById(req.id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ROOM_NOT_FOUND} with id ${req.id}",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        // Update hotel if provided
        if (req.hotelId != null) {
            val hotel = hotelRepository.findHotelActiveById(req.hotelId).orElseThrow {
                throw CustomException(
                    "${AppConstants.ERR_HOTEL_NOT_FOUND} with id ${req.hotelId}",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            room.hotel = hotel
        }
        
        // Check if new room number already exists in the hotel (excluding current room)
        if (req.roomNumber != null) {
            val existingRoom = roomRepository.findByRoomNumberAndHotelId(req.roomNumber, room.hotel.id).orElse(null)
            
            if (existingRoom != null && existingRoom.id != req.id) {
                throw CustomException(
                    "${AppConstants.ERR_ROOM_ALREADY_EXISTS} in hotel ${room.hotel.name}",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            
            room.roomNumber = req.roomNumber
        }
        
        if (req.type != null) {
            room.type = req.type
        }
        
        if (req.price != null) {
            room.price = req.price
        }
        
        if (req.status != null) {
            room.status = RoomStatus.valueOf(req.status)
        }
        
        // Update amenities if provided
        if (req.amenityIds != null) {
            if (req.amenityIds.isEmpty()) {
                room.amenities.clear()
            } else {
                val amenities = amenityRepository.findAllById(req.amenityIds)
                
                if (amenities.size != req.amenityIds.size) {
                    throw CustomException(
                        "One or more amenity IDs are invalid",
                        HttpStatus.BAD_REQUEST.value()
                    )
                }
                
                room.amenities.clear()
                room.amenities.addAll(amenities)
            }
        }
        
        room.updatedAt = Timestamp(System.currentTimeMillis())
        room.updatedBy = userId
        
        val updatedRoom = roomRepository.save(room)
        
        return mapToResRoom(updatedRoom)
    }
    
    @Transactional
    override fun deleteRoom(id: Int) {
        requireAdmin()
        
        val room = roomRepository.findRoomActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ROOM_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        room.isDelete = true
        room.deletedAt = Timestamp(System.currentTimeMillis())
        room.deletedBy = userId
        
        roomRepository.save(room)
    }
    
    @Transactional
    override fun restoreRoom(id: Int): ResRoom {
        requireAdmin()
        
        val room = roomRepository.findById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ROOM_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        if (!room.isDelete) {
            throw CustomException(
                "${AppConstants.ERR_ROOM_ALREADY_ACTIVE} with id $id",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        room.isDelete = false
        room.deletedAt = null
        room.deletedBy = null
        room.updatedAt = Timestamp(System.currentTimeMillis())
        room.updatedBy = userId
        
        val restoredRoom = roomRepository.save(room)
        
        return mapToResRoom(restoredRoom)
    }
}

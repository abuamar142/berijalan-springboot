package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.constant.AppConstants
import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateRoom
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateRoom
import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity
import com.abuamar.hotel_management_service.domain.dto.res.ResRoom
import com.abuamar.hotel_management_service.domain.entity.MasterRoomEntity
import com.abuamar.hotel_management_service.domain.enum.RoomStatus
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterAmenityRepository
import com.abuamar.hotel_management_service.repository.MasterRoomRepository
import com.abuamar.hotel_management_service.repository.RoomAmenityRepository
import com.abuamar.hotel_management_service.rest.OrderClient
import com.abuamar.hotel_management_service.service.RoomService
import com.abuamar.hotel_management_service.domain.entity.RoomAmenityEntity
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class RoomServiceImpl(
    private val roomRepository: MasterRoomRepository,
    private val amenityRepository: MasterAmenityRepository,
    private val roomAmenityRepository: RoomAmenityRepository,
    private val orderClient: OrderClient,
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
            capacity = room.capacity,
            description = room.description,
            status = room.status.name,
            amenities = room.amenities.map { amenity ->
                ResAmenity(
                    id = amenity.id,
                    name = amenity.name,
                    description = amenity.description,
                    icon = amenity.icon
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

        // Filter to show only AVAILABLE and MAINTENANCE rooms
        val filteredRooms = rooms.filter { room ->
            room.status == RoomStatus.AVAILABLE || room.status == RoomStatus.MAINTENANCE
        }

        if (filteredRooms.isEmpty()) {
            throw CustomException(
                AppConstants.ERR_NO_ROOMS_FOUND,
                HttpStatus.NOT_FOUND.value()
            )
        }

        return filteredRooms
            .sortedBy { it.roomNumber }
            .map { mapToResRoom(it) }
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

    override fun getAvailableRooms(): List<ResRoom> {
        val rooms = roomRepository.findAvailableRooms()
        
        if (rooms.isEmpty()) {
            throw CustomException(
                AppConstants.ERR_NO_ROOMS_FOUND,
                HttpStatus.NOT_FOUND.value()
            )
        }

        return rooms.map { mapToResRoom(it) }
    }

    override fun getAvailableRoomsByDate(checkInDate: String, checkOutDate: String): List<ResRoom> {
        // Get all rooms with status AVAILABLE only (exclude MAINTENANCE)
        val availableRooms = roomRepository.findByStatusAndIsDeleteFalse("AVAILABLE")
        
        if (availableRooms.isEmpty()) {
            throw CustomException(
                "No available rooms found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        // Filter rooms by checking with OrderClient
        val availableRoomsByDate = availableRooms.filter { room ->
            try {
                val isReserved = orderClient.checkRoomAvailability(
                    room.id,
                    checkInDate,
                    checkOutDate
                ).body?.data ?: false
                
                // Keep only if NOT reserved
                !isReserved
            } catch (e: Exception) {
                // If feign call fails, assume room is available
                true
            }
        }

        if (availableRoomsByDate.isEmpty()) {
            throw CustomException(
                "No rooms available for the selected dates",
                HttpStatus.NOT_FOUND.value()
            )
        }

        // Sort by room number
        return availableRoomsByDate
            .sortedBy { it.roomNumber }
            .map { mapToResRoom(it) }
    }
    
    @Transactional
    override fun createRoom(req: ReqCreateRoom): ResRoom {
        requireAdmin()
        
        // Check if room number already exists
        val existingRoom = roomRepository.findByRoomNumber(req.roomNumber).orElse(null)
        
        if (existingRoom != null) {
            throw CustomException(
                AppConstants.ERR_ROOM_ALREADY_EXISTS,
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        val room = MasterRoomEntity(
            roomNumber = req.roomNumber,
            type = req.type,
            price = req.price,
            capacity = req.capacity,
            description = req.description,
            status = RoomStatus.valueOf(req.status ?: "AVAILABLE")
        )
        
        room.createdBy = userId
        room.updatedAt = Timestamp(System.currentTimeMillis())
        room.updatedBy = userId
        room.isActive = true
        room.isDelete = false
        
        val savedRoom = roomRepository.save(room)
        
        // Handle amenities if provided
        if (!req.amenityIds.isNullOrEmpty()) {
            val amenities = amenityRepository.findAllById(req.amenityIds)
            
            if (amenities.size != req.amenityIds.size) {
                throw CustomException(
                    "One or more amenity IDs are invalid",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            
            // Create junction table entries with created_by
            val roomAmenities = amenities.map { amenity ->
                RoomAmenityEntity(
                    room = savedRoom,
                    amenity = amenity,
                    createdAt = Timestamp(System.currentTimeMillis()),
                    createdBy = userId
                )
            }
            
            roomAmenityRepository.saveAll(roomAmenities)
        }
        
        // Refresh room data to load amenities
        val roomWithAmenities = roomRepository.findRoomActiveById(savedRoom.id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ROOM_NOT_FOUND} with id ${savedRoom.id}",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        return mapToResRoom(roomWithAmenities)
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
        
        // Check if new room number already exists (excluding current room)
        if (req.roomNumber != null) {
            val existingRoom = roomRepository.findByRoomNumber(req.roomNumber).orElse(null)
            
            if (existingRoom != null && existingRoom.id != req.id) {
                throw CustomException(
                    AppConstants.ERR_ROOM_ALREADY_EXISTS,
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
        
        if (req.capacity != null) {
            room.capacity = req.capacity
        }
        
        if (req.description != null) {
            room.description = req.description
        }
        
        if (req.status != null) {
            room.status = RoomStatus.valueOf(req.status)
        }
        
        room.updatedAt = Timestamp(System.currentTimeMillis())
        room.updatedBy = userId
        
        val updatedRoom = roomRepository.save(room)
        
        // Update amenities if provided
        if (req.amenityIds != null) {
            roomAmenityRepository.deleteByRoomId(room.id)
            
            if (req.amenityIds.isNotEmpty()) {
                val amenities = amenityRepository.findAllById(req.amenityIds)
                
                if (amenities.size != req.amenityIds.size) {
                    throw CustomException(
                        "One or more amenity IDs are invalid",
                        HttpStatus.BAD_REQUEST.value()
                    )
                }
                
                val roomAmenities = amenities.map { amenity ->
                    RoomAmenityEntity(
                        room = updatedRoom,
                        amenity = amenity,
                        createdAt = Timestamp(System.currentTimeMillis()),
                        createdBy = userId
                    )
                }
                
                roomAmenityRepository.saveAll(roomAmenities)
            }
        }
        
        // Refresh room data to load amenities
        val roomWithAmenities = roomRepository.findRoomActiveById(updatedRoom.id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ROOM_NOT_FOUND} with id ${updatedRoom.id}",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        return mapToResRoom(roomWithAmenities)
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

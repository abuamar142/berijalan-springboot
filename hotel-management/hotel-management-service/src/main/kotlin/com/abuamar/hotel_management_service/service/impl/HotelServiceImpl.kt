package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.constant.AppConstants
import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateHotel
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateHotel
import com.abuamar.hotel_management_service.domain.dto.res.ResFacility
import com.abuamar.hotel_management_service.domain.dto.res.ResHotel
import com.abuamar.hotel_management_service.domain.entity.MasterHotelEntity
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterFacilityRepository
import com.abuamar.hotel_management_service.repository.MasterHotelRepository
import com.abuamar.hotel_management_service.service.HotelService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class HotelServiceImpl(
    private val hotelRepository: MasterHotelRepository,
    private val facilityRepository: MasterFacilityRepository,
    private val httpServletRequest: HttpServletRequest
): HotelService {
    
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
    
    private fun mapToResHotel(hotel: MasterHotelEntity): ResHotel {
        return ResHotel(
            id = hotel.id,
            name = hotel.name,
            address = hotel.address ?: "",
            phoneNumber = hotel.phoneNumber ?: "",
            email = hotel.email ?: "",
            rating = hotel.rating ?: 0.0,
            facilities = hotel.facilities.map { facility ->
                ResFacility(
                    id = facility.id,
                    name = facility.name,
                    description = facility.description ?: ""
                )
            }
        )
    }
    
    override fun getAllHotels(): List<ResHotel> {
        val hotels = hotelRepository.findAll()
        
        if (hotels.isEmpty()) {
            throw CustomException(
                AppConstants.ERR_NO_HOTELS_FOUND,
                HttpStatus.NOT_FOUND.value()
            )
        }

        return hotels.map { mapToResHotel(it) }
    }

    override fun getHotelById(id: Int): ResHotel {
        val hotel = hotelRepository.findHotelActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_HOTEL_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return mapToResHotel(hotel)
    }
    
    @Transactional
    override fun createHotel(req: ReqCreateHotel): ResHotel {
        requireAdmin()
        
        // Check if hotel with same name already exists
        val existingHotel = hotelRepository.findByName(req.name).orElse(null)
        
        if (existingHotel != null) {
            throw CustomException(
                "${AppConstants.ERR_HOTEL_ALREADY_EXISTS} with name ${req.name}",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        val hotel = MasterHotelEntity(
            name = req.name,
            address = req.address,
            phoneNumber = req.phoneNumber,
            email = req.email,
            rating = req.rating
        )
        
        // Handle facilities if provided
        if (!req.facilityIds.isNullOrEmpty()) {
            val facilities = facilityRepository.findAllById(req.facilityIds)
            
            if (facilities.size != req.facilityIds.size) {
                throw CustomException(
                    "One or more facility IDs are invalid",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            
            hotel.facilities = facilities.toMutableSet()
        }
        
        hotel.createdBy = userId
        hotel.updatedAt = Timestamp(System.currentTimeMillis())
        hotel.updatedBy = userId
        hotel.isActive = true
        hotel.isDelete = false
        
        val savedHotel = hotelRepository.save(hotel)
        
        return mapToResHotel(savedHotel)
    }
    
    @Transactional
    override fun updateHotel(req: ReqUpdateHotel): ResHotel {
        requireAdmin()
        
        val hotel = hotelRepository.findHotelActiveById(req.id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_HOTEL_NOT_FOUND} with id ${req.id}",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        // Check if new name already exists (excluding current hotel)
        if (req.name != null) {
            val existingHotel = hotelRepository.findByName(req.name).orElse(null)
            
            if (existingHotel != null && existingHotel.id != req.id) {
                throw CustomException(
                    "${AppConstants.ERR_HOTEL_ALREADY_EXISTS} with name ${req.name}",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            
            hotel.name = req.name
        }
        
        if (req.address != null) {
            hotel.address = req.address
        }
        
        if (req.phoneNumber != null) {
            hotel.phoneNumber = req.phoneNumber
        }
        
        if (req.email != null) {
            hotel.email = req.email
        }
        
        if (req.rating != null) {
            hotel.rating = req.rating
        }
        
        // Update facilities if provided
        if (req.facilityIds != null) {
            if (req.facilityIds.isEmpty()) {
                hotel.facilities.clear()
            } else {
                val facilities = facilityRepository.findAllById(req.facilityIds)
                
                if (facilities.size != req.facilityIds.size) {
                    throw CustomException(
                        "One or more facility IDs are invalid",
                        HttpStatus.BAD_REQUEST.value()
                    )
                }
                
                hotel.facilities.clear()
                hotel.facilities.addAll(facilities)
            }
        }
        
        hotel.updatedAt = Timestamp(System.currentTimeMillis())
        hotel.updatedBy = userId
        
        val updatedHotel = hotelRepository.save(hotel)
        
        return mapToResHotel(updatedHotel)
    }
    
    @Transactional
    override fun deleteHotel(id: Int) {
        requireAdmin()
        
        val hotel = hotelRepository.findHotelActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_HOTEL_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        hotel.isDelete = true
        hotel.deletedAt = Timestamp(System.currentTimeMillis())
        hotel.deletedBy = userId
        
        hotelRepository.save(hotel)
    }
    
    @Transactional
    override fun restoreHotel(id: Int): ResHotel {
        requireAdmin()
        
        val hotel = hotelRepository.findById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_HOTEL_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        if (!hotel.isDelete) {
            throw CustomException(
                "${AppConstants.ERR_HOTEL_ALREADY_ACTIVE} with id $id",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        hotel.isDelete = false
        hotel.deletedAt = null
        hotel.deletedBy = null
        hotel.updatedAt = Timestamp(System.currentTimeMillis())
        hotel.updatedBy = userId
        
        val restoredHotel = hotelRepository.save(hotel)
        
        return mapToResHotel(restoredHotel)
    }
}

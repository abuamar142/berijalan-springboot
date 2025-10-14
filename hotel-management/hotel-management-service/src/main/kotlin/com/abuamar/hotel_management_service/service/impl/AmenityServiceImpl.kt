package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.constant.AppConstants
import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateAmenity
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateAmenity
import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity
import com.abuamar.hotel_management_service.domain.entitiy.MasterAmenityEntity
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterAmenityRepository
import com.abuamar.hotel_management_service.service.AmenityService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class AmenityServiceImpl(
    private val amenityRepository: MasterAmenityRepository,
    private val httpServletRequest: HttpServletRequest
): AmenityService {
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
    
    override fun getAllAmenities(): List<ResAmenity> {
        val amenities = amenityRepository.findAll()
        
        if (amenities.isEmpty()) {
            throw CustomException(
                AppConstants.ERR_NO_AMENITIES_FOUND,
                HttpStatus.NOT_FOUND.value()
            )
        }

        return amenities.map { amenity ->
            ResAmenity(
                id = amenity.id,
                name = amenity.name,
                description = amenity.description ?: "",
            )
        }
    }

    override fun getAmenityById(id: Int): ResAmenity {
        val amenity = amenityRepository.findAmenityActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_AMENITY_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return ResAmenity(
            id = amenity.id,
            name = amenity.name,
            description = amenity.description ?: "",
        )
    }
    
    @Transactional
    override fun createAmenity(req: ReqCreateAmenity): ResAmenity {
        requireAdmin()
        
        // Check if amenity with same name already exists
        val existingAmenity = amenityRepository.findByName(req.name).orElse(null)
        
        if (existingAmenity != null) {
            throw CustomException(
                "${AppConstants.ERR_AMENITY_ALREADY_EXISTS} with name ${req.name}",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        val amenity = MasterAmenityEntity(
            name = req.name,
            description = req.description
        )
        
        amenity.createdBy = userId

        amenity.updatedAt = Timestamp(System.currentTimeMillis())
        amenity.updatedBy = userId

        amenity.isActive = true
        amenity.isDelete = false
        
        val savedAmenity = amenityRepository.save(amenity)
        
        return ResAmenity(
            id = savedAmenity.id,
            name = savedAmenity.name,
            description = savedAmenity.description ?: ""
        )
    }
    
    @Transactional
    override fun updateAmenity(req: ReqUpdateAmenity): ResAmenity {
        requireAdmin()
        
        val amenity = amenityRepository.findAmenityActiveById(req.id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_AMENITY_NOT_FOUND} with id ${req.id}",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        // Check if new name already exists (excluding current amenity)
        if (req.name != null) {
            val existingAmenity = amenityRepository.findByName(req.name).orElse(null)
            
            if (existingAmenity != null && existingAmenity.id != req.id) {
                throw CustomException(
                    "${AppConstants.ERR_AMENITY_ALREADY_EXISTS} with name ${req.name}",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            
            amenity.name = req.name
        }
        
        if (req.description != null) {
            amenity.description = req.description
        }
        
        amenity.updatedAt = Timestamp(System.currentTimeMillis())
        amenity.updatedBy = userId
        
        val updatedAmenity = amenityRepository.save(amenity)
        
        return ResAmenity(
            id = updatedAmenity.id,
            name = updatedAmenity.name,
            description = updatedAmenity.description ?: ""
        )
    }
    
    @Transactional
    override fun deleteAmenity(id: Int) {
        requireAdmin()
        
        val amenity = amenityRepository.findAmenityActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_AMENITY_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        amenity.isDelete = true
        amenity.deletedAt = Timestamp(System.currentTimeMillis())
        amenity.deletedBy = userId
        
        amenityRepository.save(amenity)
    }
    
    @Transactional
    override fun restoreAmenity(id: Int): ResAmenity {
        requireAdmin()
        
        val amenity = amenityRepository.findById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_AMENITY_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        if (!amenity.isDelete) {
            throw CustomException(
                "${AppConstants.ERR_AMENITY_ALREADY_ACTIVE} with id $id",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        amenity.isDelete = false
        amenity.deletedAt = null
        amenity.deletedBy = null

        amenity.updatedAt = Timestamp(System.currentTimeMillis())
        amenity.updatedBy = userId
        
        val restoredAmenity = amenityRepository.save(amenity)
        
        return ResAmenity(
            id = restoredAmenity.id,
            name = restoredAmenity.name,
            description = restoredAmenity.description ?: ""
        )
    }
}
package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.constant.AppConstants
import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateFacility
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateFacility
import com.abuamar.hotel_management_service.domain.dto.res.ResFacility
import com.abuamar.hotel_management_service.domain.entity.MasterFacilityEntity
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterFacilityRepository
import com.abuamar.hotel_management_service.service.FacilityService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class FacilityServiceImpl(
    private val facilityRepository: MasterFacilityRepository,
    private val httpServletRequest: HttpServletRequest
): FacilityService {
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
    
    override fun getAllFacilities(): List<ResFacility> {
        val facilities = facilityRepository.findAll()
        
        if (facilities.isEmpty()) {
            throw CustomException(
                AppConstants.ERR_NO_FACILITIES_FOUND,
                HttpStatus.NOT_FOUND.value()
            )
        }

        return facilities.map { facility ->
            ResFacility(
                id = facility.id,
                name = facility.name,
                description = facility.description ?: "",
            )
        }
    }

    override fun getFacilityById(id: Int): ResFacility {
        val facility = facilityRepository.findFacilityActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_FACILITY_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return ResFacility(
            id = facility.id,
            name = facility.name,
            description = facility.description ?: "",
        )
    }
    
    @Transactional
    override fun createFacility(req: ReqCreateFacility): ResFacility {
        requireAdmin()
        
        // Check if facility with same name already exists
        val existingFacility = facilityRepository.findByName(req.name).orElse(null)
        
        if (existingFacility != null) {
            throw CustomException(
                "${AppConstants.ERR_FACILITY_ALREADY_EXISTS} with name ${req.name}",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        val facility = MasterFacilityEntity(
            name = req.name,
            description = req.description
        )
        
        facility.createdBy = userId
        facility.updatedAt = Timestamp(System.currentTimeMillis())
        facility.updatedBy = userId
        facility.isActive = true
        facility.isDelete = false
        
        val savedFacility = facilityRepository.save(facility)
        
        return ResFacility(
            id = savedFacility.id,
            name = savedFacility.name,
            description = savedFacility.description ?: ""
        )
    }
    
    @Transactional
    override fun updateFacility(req: ReqUpdateFacility): ResFacility {
        requireAdmin()
        
        val facility = facilityRepository.findFacilityActiveById(req.id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_FACILITY_NOT_FOUND} with id ${req.id}",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        // Check if new name already exists (excluding current facility)
        if (req.name != null) {
            val existingFacility = facilityRepository.findByName(req.name).orElse(null)
            
            if (existingFacility != null && existingFacility.id != req.id) {
                throw CustomException(
                    "${AppConstants.ERR_FACILITY_ALREADY_EXISTS} with name ${req.name}",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            
            facility.name = req.name
        }
        
        if (req.description != null) {
            facility.description = req.description
        }
        
        facility.updatedAt = Timestamp(System.currentTimeMillis())
        facility.updatedBy = userId
        
        val updatedFacility = facilityRepository.save(facility)
        
        return ResFacility(
            id = updatedFacility.id,
            name = updatedFacility.name,
            description = updatedFacility.description ?: ""
        )
    }
    
    @Transactional
    override fun deleteFacility(id: Int) {
        requireAdmin()
        
        val facility = facilityRepository.findFacilityActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_FACILITY_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        facility.isDelete = true
        facility.deletedAt = Timestamp(System.currentTimeMillis())
        facility.deletedBy = userId
        
        facilityRepository.save(facility)
    }
    
    @Transactional
    override fun restoreFacility(id: Int): ResFacility {
        requireAdmin()
        
        val facility = facilityRepository.findById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_FACILITY_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        if (!facility.isDelete) {
            throw CustomException(
                "${AppConstants.ERR_FACILITY_ALREADY_ACTIVE} with id $id",
                HttpStatus.BAD_REQUEST.value()
            )
        }
        
        val userId = getAuthenticatedUserId()
        
        facility.isDelete = false
        facility.deletedAt = null
        facility.deletedBy = null
        facility.updatedAt = Timestamp(System.currentTimeMillis())
        facility.updatedBy = userId
        
        val restoredFacility = facilityRepository.save(facility)
        
        return ResFacility(
            id = restoredFacility.id,
            name = restoredFacility.name,
            description = restoredFacility.description ?: ""
        )
    }
}
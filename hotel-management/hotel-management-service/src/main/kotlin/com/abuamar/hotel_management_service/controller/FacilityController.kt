package com.abuamar.hotel_management_service.controller

import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateFacility
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateFacility
import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import com.abuamar.hotel_management_service.domain.dto.res.ResFacility
import com.abuamar.hotel_management_service.service.FacilityService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/facilities")
class FacilityController(
    private val facilityService: FacilityService
) {
    
    @GetMapping
    fun getAllFacilities(): ResponseEntity<BaseResponse<List<ResFacility>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all facilities",
                data = facilityService.getAllFacilities(),
            )
        )
    }

    @GetMapping("/{id}")
    fun getFacilityById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResFacility>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get facility by id $id",
                data = facilityService.getFacilityById(id),
            )
        )
    }
    
    @PostMapping
    fun createFacility(
        @RequestBody @Valid req: ReqCreateFacility
    ): ResponseEntity<BaseResponse<ResFacility>> {
        return ResponseEntity(
            BaseResponse(
                success = true,
                message = "Success create facility",
                data = facilityService.createFacility(req),
            ),
            HttpStatus.CREATED
        )
    }
    
    @PutMapping("/{id}")
    fun updateFacility(
        @PathVariable id: Int,
        @RequestBody @Valid req: ReqUpdateFacility
    ): ResponseEntity<BaseResponse<ResFacility>> {
        req.id = id
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success update facility with id $id",
                data = facilityService.updateFacility(req),
            )
        )
    }
    
    @DeleteMapping("/{id}")
    fun deleteFacility(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        facilityService.deleteFacility(id)
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success delete facility with id $id",
                data = null
            )
        )
    }
    
    @PatchMapping("/{id}")
    fun restoreFacility(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResFacility>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success restore facility with id $id",
                data = facilityService.restoreFacility(id)
            )
        )
    }
}

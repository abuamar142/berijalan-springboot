package com.abuamar.hotel_management_service.controller

import com.abuamar.hotel_management_service.domain.constant.AppConstants
import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateAmenity
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateAmenity
import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity
import com.abuamar.hotel_management_service.service.AmenityService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/amenities")
class AmenityController(
    private val amenityService: AmenityService
) {
    @GetMapping
    fun getAllAmenities(): ResponseEntity<BaseResponse<List<ResAmenity>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all amenities",
                data = amenityService.getAllAmenities(),
            )
        )
    }

    @GetMapping("/{id}")
    fun getAmenityById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResAmenity>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get amenity by id $id",
                data = amenityService.getAmenityById(id),
            )
        )
    }
    
    @PostMapping
    fun createAmenity(
        @RequestBody @Valid req: ReqCreateAmenity
    ): ResponseEntity<BaseResponse<ResAmenity>> {
        return ResponseEntity(
            BaseResponse(
                success = true,
                message = "Success create amenity",
                data = amenityService.createAmenity(req),
            ),
            HttpStatus.CREATED
        )
    }
    
    @PutMapping("/{id}")
    fun updateAmenity(
        @PathVariable id: Int,
        @RequestBody @Valid req: ReqUpdateAmenity
    ): ResponseEntity<BaseResponse<ResAmenity>> {
        req.id = id
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success update amenity with id $id",
                data = amenityService.updateAmenity(req),
            )
        )
    }
    
    @DeleteMapping("/{id}")
    fun deleteAmenity(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        amenityService.deleteAmenity(id)
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success delete amenity with id $id",
                data = null
            )
        )
    }
    
    @PatchMapping("/{id}")
    fun restoreAmenity(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResAmenity>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success restore amenity with id $id",
                data = amenityService.restoreAmenity(id)
            )
        )
    }
}
package com.abuamar.hotel_management_service.controller

import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import com.abuamar.hotel_management_service.domain.dto.res.ResAmenity
import com.abuamar.hotel_management_service.service.AmenityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/amenities")
class AmenityController(
    private val amenityService: AmenityService
) {
    @GetMapping
    fun getAmenities(): ResponseEntity<BaseResponse<List<ResAmenity>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all amenities",
                data = amenityService.getAmenities(),
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
}
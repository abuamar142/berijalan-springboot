package com.abuamar.hotel_management_service.controller

import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateHotel
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateHotel
import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import com.abuamar.hotel_management_service.domain.dto.res.ResHotel
import com.abuamar.hotel_management_service.service.HotelService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hotels")
class HotelController(
    private val hotelService: HotelService
) {
    
    @GetMapping
    fun getAllHotels(): ResponseEntity<BaseResponse<List<ResHotel>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all hotels",
                data = hotelService.getAllHotels(),
            )
        )
    }

    @GetMapping("/{id}")
    fun getHotelById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResHotel>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get hotel by id $id",
                data = hotelService.getHotelById(id),
            )
        )
    }
    
    @PostMapping
    fun createHotel(
        @RequestBody @Valid req: ReqCreateHotel
    ): ResponseEntity<BaseResponse<ResHotel>> {
        return ResponseEntity(
            BaseResponse(
                success = true,
                message = "Success create hotel",
                data = hotelService.createHotel(req),
            ),
            HttpStatus.CREATED
        )
    }
    
    @PutMapping("/{id}")
    fun updateHotel(
        @PathVariable id: Int,
        @RequestBody @Valid req: ReqUpdateHotel
    ): ResponseEntity<BaseResponse<ResHotel>> {
        req.id = id
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success update hotel with id $id",
                data = hotelService.updateHotel(req),
            )
        )
    }
    
    @DeleteMapping("/{id}")
    fun deleteHotel(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        hotelService.deleteHotel(id)
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success delete hotel with id $id",
                data = null
            )
        )
    }
    
    @PatchMapping("/{id}")
    fun restoreHotel(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResHotel>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success restore hotel with id $id",
                data = hotelService.restoreHotel(id)
            )
        )
    }
}

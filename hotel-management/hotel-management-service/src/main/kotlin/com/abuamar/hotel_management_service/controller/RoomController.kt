package com.abuamar.hotel_management_service.controller

import com.abuamar.hotel_management_service.domain.dto.req.ReqCreateRoom
import com.abuamar.hotel_management_service.domain.dto.req.ReqUpdateRoom
import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import com.abuamar.hotel_management_service.domain.dto.res.ResRoom
import com.abuamar.hotel_management_service.service.RoomService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val roomService: RoomService
) {
    
    @GetMapping
    fun getAllRooms(): ResponseEntity<BaseResponse<List<ResRoom>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all rooms",
                data = roomService.getAllRooms(),
            )
        )
    }

    @GetMapping("/{id}")
    fun getRoomById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResRoom>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get room by id $id",
                data = roomService.getRoomById(id),
            )
        )
    }
    
    @PostMapping
    fun createRoom(
        @RequestBody @Valid req: ReqCreateRoom
    ): ResponseEntity<BaseResponse<ResRoom>> {
        return ResponseEntity(
            BaseResponse(
                success = true,
                message = "Success create room",
                data = roomService.createRoom(req),
            ),
            HttpStatus.CREATED
        )
    }
    
    @PutMapping("/{id}")
    fun updateRoom(
        @PathVariable id: Int,
        @RequestBody @Valid req: ReqUpdateRoom
    ): ResponseEntity<BaseResponse<ResRoom>> {
        req.id = id
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success update room with id $id",
                data = roomService.updateRoom(req),
            )
        )
    }
    
    @DeleteMapping("/{id}")
    fun deleteRoom(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        roomService.deleteRoom(id)
        
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success delete room with id $id",
                data = null
            )
        )
    }
    
    @PatchMapping("/{id}")
    fun restoreRoom(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResRoom>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success restore room with id $id",
                data = roomService.restoreRoom(id)
            )
        )
    }
}

package com.abuamar.order_management_service.rest

import com.abuamar.order_management_service.domain.dto.res.BaseResponse
import com.abuamar.order_management_service.domain.dto.res.ResRoom
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "hotel-management-service", path = "/hotel-management")
interface RoomClient {
    @GetMapping("/rooms/{id}")
    fun getRoomById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResRoom>>
}

package com.abuamar.hotel_management_service.rest

import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "order-management-service", path = "/order-management")
interface OrderClient {
    @GetMapping("/orders/check-availability")
    fun checkRoomAvailability(
        @RequestParam roomId: Int,
        @RequestParam checkInDate: String,
        @RequestParam checkOutDate: String
    ): ResponseEntity<BaseResponse<Boolean>>
}

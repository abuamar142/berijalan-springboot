package com.abuamar.hotel_management_service.rest

import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import com.abuamar.hotel_management_service.domain.dto.res.ResUserById
import com.abuamar.hotel_management_service.domain.dto.res.ResUserId
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "user-management-service", path = "/user-management")
interface UserClient {
    @GetMapping("users/{id}")
    fun getUserById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResUserById>>

    @GetMapping("/data-source/users-by-ids")
    fun getUsersByIds(
        @RequestParam(required = true) userIds: List<Int>
    ): ResponseEntity<BaseResponse<List<ResUserId>>>
}
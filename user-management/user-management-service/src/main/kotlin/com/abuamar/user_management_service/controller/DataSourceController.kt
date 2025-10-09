package com.abuamar.user_management_service.controller

import com.abuamar.user_management_service.domain.dto.res.BaseResponse
import com.abuamar.user_management_service.domain.dto.res.ResUserId
import com.abuamar.user_management_service.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data-source")
class DataSourceController(
    private val userService: UserService
) {
    @GetMapping("/users-by-ids")
    fun getUsersByIds(
        @RequestParam(required = true) userIds: List<Int>
    ): ResponseEntity<BaseResponse<List<ResUserId>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get users by ids",
                data = userService.getUsersByUniqueIds(userIds),
            )
        )
    }
}
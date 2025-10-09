package com.abuamar.user_management_service.controller

import com.abuamar.user_management_service.domain.dto.res.BaseResponse
import com.abuamar.user_management_service.domain.dto.res.ResRole
import com.abuamar.user_management_service.service.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roles")
class RoleController(
    private val roleService: RoleService
) {
    @GetMapping
    fun getAllRoles(): ResponseEntity<BaseResponse<List<ResRole>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all roles",
                data = roleService.findRoles(),
            )
        )
    }
}
package com.abuamar.user_management_service.controller

import com.abuamar.user_management_service.domain.dto.req.ReqUserUpdate
import com.abuamar.user_management_service.domain.dto.res.BaseResponse
import com.abuamar.user_management_service.domain.dto.res.ResUser
import com.abuamar.user_management_service.domain.dto.res.ResUserById
import com.abuamar.user_management_service.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService

) {
    @GetMapping
    fun getAllUser(): ResponseEntity<BaseResponse<List<ResUser>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all user",
                data = userService.findAllUser(),
            )
        )
    }

    @PutMapping("/{id}")
    fun updateUserById(
        @PathVariable id: Int,
        @RequestBody @Valid req: ReqUserUpdate
    ): ResponseEntity<BaseResponse<ResUserById>> {
        req.id = id

        val response: ResUserById = userService.updateUserById(req)
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                data = response,
                message = "Success update user with id $id",
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteUserById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        userService.deleteUserById(id)
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success delete user with id $id"
            )
        )
    }

    @PatchMapping("/{id}")
    fun restoreUserById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        userService.restoreUserById(id)
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success restore user with id $id"
            )
        )
    }

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResUserById>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get user by id $id",
                data = userService.findUserById(id),
            )
        )
    }

    @DeleteMapping("/bulk-delete")
    fun bulkDeleteUsers(
        @RequestBody req: List<Int>
    ): ResponseEntity<BaseResponse<String>> {
        userService.bulkDeleteUsers(req)
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success bulk delete users"
            )
        )
    }
}
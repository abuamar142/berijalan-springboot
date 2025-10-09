package com.abuamar.user_management_service.controller

import com.abuamar.user_management_service.domain.dto.res.BaseResponse
import com.abuamar.user_management_service.service.DummyJsonService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dummy-json")
class DummyJsonController(
     private val dummyJsonService: DummyJsonService
) {
    @GetMapping("/products")
    fun getProducts(): ResponseEntity<BaseResponse<Any>> {
        return ResponseEntity(
            BaseResponse(
                message = "Products fetched successfully",
                data = dummyJsonService.getProducts()
            ),
            HttpStatus.OK
        )
    }
}
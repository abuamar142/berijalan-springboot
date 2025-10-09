package com.abuamar.user_management_service.rest

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "dummy-json-client",
    url = "https://dummyjson.com",
    path = "/products",
)
interface DummyJsonClient {
    @GetMapping
    fun getProducts(): Any
}
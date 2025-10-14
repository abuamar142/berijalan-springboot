package com.abuamar.hotel_management_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["com.abuamar.hotel_management_service"])
@EnableCaching
class HotelManagementApplication

fun main(args: Array<String>) {
	runApplication<HotelManagementApplication>(*args)
}

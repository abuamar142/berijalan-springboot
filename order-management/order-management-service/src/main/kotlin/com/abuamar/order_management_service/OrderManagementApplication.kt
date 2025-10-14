package com.abuamar.order_management_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class OrderManagementApplication

fun main(args: Array<String>) {
	runApplication<OrderManagementApplication>(*args)
}

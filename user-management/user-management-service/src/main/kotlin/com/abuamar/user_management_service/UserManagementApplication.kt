package com.abuamar.user_management_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["com.abuamar.user_management_service"])
class UserManagementApplication

fun main(args: Array<String>) {
	runApplication<UserManagementApplication>(*args)
}

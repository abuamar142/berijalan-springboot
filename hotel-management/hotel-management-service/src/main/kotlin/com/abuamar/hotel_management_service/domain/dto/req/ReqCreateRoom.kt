package com.abuamar.hotel_management_service.domain.dto.req

import jakarta.validation.constraints.*

data class ReqCreateRoom(
    @field:NotBlank(message = "Room number cannot be blank")
    @field:Size(min = 1, max = 20, message = "Room number must be between 1 and 20 characters")
    val roomNumber: String,

    @field:NotBlank(message = "Room type cannot be blank")
    @field:Size(min = 3, max = 50, message = "Room type must be between 3 and 50 characters")
    val type: String,

    @field:NotNull(message = "Price cannot be null")
    @field:Min(value = 0, message = "Price must be at least 0")
    val price: Int,

    @field:NotNull(message = "Capacity cannot be null")
    @field:Min(value = 1, message = "Capacity must be at least 1")
    val capacity: Int,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

    @field:Pattern(
        regexp = "^(AVAILABLE|MAINTENANCE|RESERVED)$",
        message = "Status must be AVAILABLE, MAINTENANCE, or RESERVED"
    )
    val status: String? = "AVAILABLE",

    val amenityIds: List<Int>? = null
)

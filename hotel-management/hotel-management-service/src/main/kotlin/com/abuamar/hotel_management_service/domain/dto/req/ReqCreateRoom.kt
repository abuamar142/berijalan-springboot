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
    @field:DecimalMin(value = "0.0", message = "Price must be at least 0.0")
    val price: Double,

    @field:NotNull(message = "Hotel ID cannot be null")
    @field:Min(value = 1, message = "Hotel ID must be at least 1")
    val hotelId: Int,

    @field:Pattern(
        regexp = "^(AVAILABLE|OCCUPIED|MAINTENANCE|RESERVED)$",
        message = "Status must be AVAILABLE, OCCUPIED, MAINTENANCE, or RESERVED"
    )
    val status: String? = "AVAILABLE",

    val amenityIds: List<Int>? = null
)

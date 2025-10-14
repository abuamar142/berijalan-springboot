package com.abuamar.hotel_management_service.domain.dto.req

import jakarta.validation.constraints.*

data class ReqUpdateRoom(
    var id: Int = 0,

    @field:Size(min = 1, max = 20, message = "Room number must be between 1 and 20 characters")
    val roomNumber: String? = null,

    @field:Size(min = 3, max = 50, message = "Room type must be between 3 and 50 characters")
    val type: String? = null,

    @field:DecimalMin(value = "0.0", message = "Price must be at least 0.0")
    val price: Double? = null,

    @field:Min(value = 1, message = "Hotel ID must be at least 1")
    val hotelId: Int? = null,

    @field:Pattern(
        regexp = "^(AVAILABLE|OCCUPIED|MAINTENANCE|RESERVED)$",
        message = "Status must be AVAILABLE, OCCUPIED, MAINTENANCE, or RESERVED"
    )
    val status: String? = null,

    val amenityIds: List<Int>? = null
)

package com.abuamar.hotel_management_service.domain.dto.req

import jakarta.validation.constraints.*

data class ReqCreateHotel(
    @field:NotBlank(message = "Hotel name cannot be blank")
    @field:Size(min = 3, max = 200, message = "Hotel name must be between 3 and 200 characters")
    val name: String,

    @field:NotBlank(message = "Address cannot be blank")
    @field:Size(max = 500, message = "Address cannot exceed 500 characters")
    val address: String,

    @field:Pattern(
        regexp = "^[+]?[0-9]{10,15}$",
        message = "Phone number must be valid (10-15 digits)"
    )
    val phoneNumber: String? = null,

    @field:Email(message = "Email must be valid")
    @field:Size(max = 100, message = "Email cannot exceed 100 characters")
    val email: String? = null,

    @field:DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @field:DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    val rating: Double? = null,

    val facilityIds: List<Int>? = null
)

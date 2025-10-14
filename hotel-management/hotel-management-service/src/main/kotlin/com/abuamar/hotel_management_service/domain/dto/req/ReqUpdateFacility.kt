package com.abuamar.hotel_management_service.domain.dto.req

import jakarta.validation.constraints.Size

data class ReqUpdateFacility(
    var id: Int = 0,

    @field:Size(min = 3, max = 100, message = "Facility name must be between 3 and 100 characters")
    val name: String? = null,

    @field:Size(max = 500, message = "Description cannot exceed 500 characters")
    val description: String? = null
)

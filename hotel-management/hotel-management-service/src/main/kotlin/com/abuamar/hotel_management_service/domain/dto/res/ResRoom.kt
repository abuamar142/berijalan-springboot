package com.abuamar.hotel_management_service.domain.dto.res

data class ResRoom(
    val id: Int,
    val roomNumber: String,
    val type: String,
    val price: Int,
    val capacity: Int,
    val description: String?,
    val status: String,
    val amenities: List<ResAmenity>
)

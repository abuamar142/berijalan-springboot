package com.abuamar.hotel_management_service.domain.dto.res

data class ResHotel(
    val id: Int,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val rating: Double,
    val facilities: List<ResFacility>
)

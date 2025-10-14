package com.abuamar.hotel_management_service.domain.dto.res

data class ResRoom(
    val id: Int,
    val roomNumber: String,
    val type: String,
    val price: Double,
    val status: String,
    val hotel: ResHotelSimple,
    val amenities: List<ResAmenity>
)

data class ResHotelSimple(
    val id: Int,
    val name: String
)

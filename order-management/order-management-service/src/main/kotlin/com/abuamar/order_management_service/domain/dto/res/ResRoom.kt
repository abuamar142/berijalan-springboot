package com.abuamar.order_management_service.domain.dto.res

data class ResRoom(
    val id: Int,
    val roomNumber: String,
    val type: String,
    val price: Int,
    val capacity: Int,
    val description: String?,
    val status: String,
    val amenities: Set<ResAmenity>
)

data class ResAmenity(
    val id: Int,
    val name: String,
    val icon: String?
)

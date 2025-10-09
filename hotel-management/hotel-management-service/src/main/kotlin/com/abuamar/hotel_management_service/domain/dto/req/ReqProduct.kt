package com.abuamar.hotel_management_service.domain.dto.req

import java.sql.Timestamp

data class ReqProduct(
    var id: Int,
    val name: String,
    val price: Long,
    val brandId: Int
)
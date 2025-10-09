package com.abuamar.user_management_service.domain.dto.req

data class ReqUserUpdate(
    var id: Int,
    val username: String?,
    val fullName: String?
)
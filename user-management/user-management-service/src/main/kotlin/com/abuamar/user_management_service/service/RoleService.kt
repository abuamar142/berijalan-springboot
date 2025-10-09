package com.abuamar.user_management_service.service

import com.abuamar.user_management_service.domain.dto.res.ResRole

interface RoleService {
    fun findRoles(): List<ResRole>
}
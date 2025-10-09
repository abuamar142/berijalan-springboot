package com.abuamar.user_management_service.repository

import com.abuamar.user_management_service.domain.entity.MasterRoleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MasterRoleRepository: JpaRepository<MasterRoleEntity, Int> {
}
package com.abuamar.user_management_service.service.impl

import com.abuamar.user_management_service.domain.dto.res.ResRole
import com.abuamar.user_management_service.domain.entity.MasterRoleEntity
import com.abuamar.user_management_service.repository.MasterRoleRepository
import com.abuamar.user_management_service.service.RoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(
     private val masterRoleRepository: MasterRoleRepository
): RoleService {
    override fun findRoles(): List<ResRole> {
        val roles: List<MasterRoleEntity> = masterRoleRepository.findAll()

        return roles.map { role ->
            ResRole(
                id = role.id,
                name = role.name
            )
        }
    }
}
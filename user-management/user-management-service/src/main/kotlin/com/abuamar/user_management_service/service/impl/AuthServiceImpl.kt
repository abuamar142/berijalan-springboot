package com.abuamar.user_management_service.service.impl

import com.abuamar.user_management_service.domain.dto.res.ResUserById
import com.abuamar.user_management_service.domain.entity.MasterUserEntity
import com.abuamar.user_management_service.domain.dto.req.ReqLogin
import com.abuamar.user_management_service.domain.dto.req.ReqRegister
import com.abuamar.user_management_service.domain.dto.res.ResLogin
import com.abuamar.user_management_service.exception.CustomException
import com.abuamar.user_management_service.repository.MasterRoleRepository
import com.abuamar.user_management_service.repository.MasterUserRepository
import com.abuamar.user_management_service.service.AuthService
import com.abuamar.user_management_service.util.JWTUtil
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val userRepository: MasterUserRepository,
    private val roleRepository: MasterRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JWTUtil
): AuthService {
    override fun login(req: ReqLogin): ResLogin {
        val user = userRepository.findByUsername(req.username) ?: throw CustomException(
            "Username or password is incorrect",
            HttpStatus.UNAUTHORIZED.value()
        )

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw CustomException(
                "Username or password is incorrect",
                HttpStatus.UNAUTHORIZED.value()
            )
        }

        return ResLogin(
            token = jwtUtil.generateToken(user.id, user.role?.name),
        )
    }

    override fun register(req: ReqRegister): ResUserById {
        val registeredUser = userRepository.findByUsername(req.username)
        if (registeredUser != null) {
           throw CustomException(
               "Username ${req.username} already registered",
               HttpStatus.BAD_REQUEST.value()
           )
        }

        val role = if (req.roleId != null) {
            roleRepository.findById(req.roleId).orElseThrow {
                throw Exception("Role with id ${req.roleId} not found")
            }
        } else {
            null
        }

        req.password = passwordEncoder.encode(req.password)

        val user = MasterUserEntity(
            username = req.username,
            password = req.password,
            fullName = req.fullName,
            role = role,
        )

        user.createdBy = "SYSTEM"

        val userDb = userRepository.save(user)

        return ResUserById(
            id = userDb.id,
            username = userDb.username,
            fullName = userDb.fullName,
            roleName = userDb.role?.name,
            createdAt = userDb.createdAt!!,
            createdBy = userDb.createdBy ?: "SYSTEM"
        )
    }

}
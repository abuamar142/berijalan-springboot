package com.abuamar.user_management_service.service.impl

import com.abuamar.user_management_service.domain.dto.req.ReqUserUpdate
import com.abuamar.user_management_service.domain.dto.res.ResUser
import com.abuamar.user_management_service.domain.dto.res.ResUserById
import com.abuamar.user_management_service.domain.dto.res.ResUserId
import com.abuamar.user_management_service.domain.constant.TopicKafka
import com.abuamar.user_management_service.exception.CustomException
import com.abuamar.user_management_service.producer.KafkaProducer
import com.abuamar.user_management_service.repository.MasterUserRepository
import com.abuamar.user_management_service.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val masterUserRepository: MasterUserRepository,
    private val httpServletRequest: HttpServletRequest,
    private val kafkaProducer: KafkaProducer<Any>,
): UserService {
    override fun findAllUser(): List<ResUser> {
        val isAdmin: Boolean = httpServletRequest.getHeader("X-USER-AUTHORITY") == "admin"

        if (!isAdmin) {
            throw CustomException(
                "You are not authorized to access this resource",
                HttpStatus.FORBIDDEN.value()
            )
        }

        val rawData = masterUserRepository.findAll()

        return rawData.map { user ->
            ResUser(
                id = user.id,
                username = user.username,
                fullName = user.fullName,
                createdAt = user.createdAt!!,
                createdBy = user.createdBy
            )
        }
    }

    override fun findUserById(id: Int): ResUserById {
        val isSelf: Boolean = httpServletRequest.getHeader("X-USER-ID")?.toInt() == id
        val isAdmin: Boolean = httpServletRequest.getHeader("X-USER-AUTHORITY") == "admin"

        if (!isSelf && !isAdmin) {
            throw CustomException(
                "You are not authorized to access this resource",
                HttpStatus.FORBIDDEN.value()
            )
        }

        val result = masterUserRepository.findById(id).orElseThrow {
            throw CustomException(
                "User with id $id not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return ResUserById(
            id = result.id,
            username = result.username,
            fullName = result.fullName,
            roleName = result.role?.name,
            createdAt = result.createdAt!!,
            createdBy = result.createdBy
        )
    }

    override fun updateUserById(req: ReqUserUpdate): ResUserById {
        val isSelf: Boolean = httpServletRequest.getHeader("X-USER-ID")?.toInt() == req.id
        val isAdmin: Boolean = httpServletRequest.getHeader("X-USER-AUTHORITY") == "admin"

        if (!isSelf && !isAdmin) {
            throw CustomException(
                "You are not authorized to update this user",
                HttpStatus.FORBIDDEN.value()
            )
        }

        val user = masterUserRepository.findById(req.id).orElseThrow {
            throw Exception("User with id ${req.id} not found")
        }

        if (req.username != null) user.username = req.username
        if (req.fullName != null) user.fullName = req.fullName

        val updatedUser = masterUserRepository.save(user)

        return ResUserById(
            id = updatedUser.id,
            username = updatedUser.username,
            fullName = updatedUser.fullName,
            roleName = updatedUser.role?.name,
            createdAt = updatedUser.createdAt!!,
            createdBy = updatedUser.createdBy
        )
    }

    override fun deleteUserById(id: Int) {
        val isAdmin: Boolean = httpServletRequest.getHeader("X-USER-AUTHORITY") == "admin"

        if (!isAdmin) {
            throw CustomException(
                "You are not authorized to delete user",
                HttpStatus.FORBIDDEN.value()
            )
        }

        val user = masterUserRepository.findById(id).orElseThrow {
            throw CustomException(
                "User with id $id not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        user.isDeleted = true

        masterUserRepository.save(user)

        kafkaProducer.sendMessage(TopicKafka.DELETE_USER_PRODUCT, user.id.toString())
    }

    override fun getUsersByUniqueIds(userIds: List<Int>): List<ResUserId> {
        return masterUserRepository.findUsersByIds(userIds).map {
            ResUserId(
                userId = it.id,
                fullName = it.fullName
            )
        }
    }
}
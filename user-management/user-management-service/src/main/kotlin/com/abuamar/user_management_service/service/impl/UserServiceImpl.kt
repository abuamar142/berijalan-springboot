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
import java.sql.Timestamp

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
                createdBy = user.createdBy ?: "SYSTEM",
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

        val result = masterUserRepository.findUserActiveById(id).orElseThrow {
            throw CustomException(
                "User with id $id not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (result.isDelete) {
            throw CustomException(
                "User with id $id is deleted",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        return ResUserById(
            id = result.id,
            username = result.username,
            fullName = result.fullName,
            roleName = result.role?.name,
            createdAt = result.createdAt!!,
            createdBy = result.createdBy ?: "SYSTEM"
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

        val user = masterUserRepository.findUserActiveById(req.id).orElseThrow {
            throw Exception("User with id ${req.id} not found")
        }

        if (user.isDelete) {
            throw CustomException(
                "User with id ${req.id} is deleted",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        val updaterId = httpServletRequest.getHeader("X-USER-ID")
        val updater = masterUserRepository.findUserActiveById(updaterId!!.toInt()).orElseThrow {
            throw CustomException(
                "Updater with id $updaterId not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        user.updatedBy = updater.fullName
        user.updatedAt = Timestamp(System.currentTimeMillis())

        if (req.username != null) user.username = req.username
        if (req.fullName != null) user.fullName = req.fullName

        val updatedUser = masterUserRepository.save(user)

        return ResUserById(
            id = updatedUser.id,
            username = updatedUser.username,
            fullName = updatedUser.fullName,
            roleName = updatedUser.role?.name,
            createdAt = updatedUser.createdAt!!,
            createdBy = updatedUser.createdBy ?: "SYSTEM"
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

        val user = masterUserRepository.findUserActiveById(id).orElseThrow {
            throw CustomException(
                "User with id $id not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (user.isDelete) {
            throw CustomException(
                "User with id $id is already deleted",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        val adminId = httpServletRequest.getHeader("X-USER-ID")
        val admin = masterUserRepository.findUserActiveById(adminId!!.toInt()).orElseThrow {
            throw CustomException(
                "Admin with id $adminId not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        user.deletedBy = admin.fullName
        user.deletedAt = Timestamp(System.currentTimeMillis())

        user.isDelete = true
        user.isActive = false

        masterUserRepository.save(user)

        kafkaProducer.sendMessage(TopicKafka.DELETE_USER_PRODUCT, user.id.toString())
    }

    override fun restoreUserById(id: Int) {
        val isAdmin: Boolean = httpServletRequest.getHeader("X-USER-AUTHORITY") == "admin"

        if (!isAdmin) {
            throw CustomException(
                "You are not authorized to restore user",
                HttpStatus.FORBIDDEN.value()
            )
        }

        val user = masterUserRepository.findById(id).orElseThrow {
            throw CustomException(
                "User with id $id not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (!user.isDelete) {
            throw CustomException(
                "User with id $id is not deleted",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        val adminId = httpServletRequest.getHeader("X-USER-ID")
        val admin = masterUserRepository.findUserActiveById(adminId!!.toInt()).orElseThrow {
            throw CustomException(
                "Admin with id $adminId not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        user.updatedBy = admin.fullName
        user.updatedAt = Timestamp(System.currentTimeMillis())

        user.deletedBy = null
        user.deletedAt = null

        user.isDelete = false
        user.isActive = true

        masterUserRepository.save(user)
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
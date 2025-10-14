package com.abuamar.user_management_service.service.impl

import com.abuamar.user_management_service.domain.constant.AppConstants
import com.abuamar.user_management_service.domain.dto.req.ReqUserUpdate
import com.abuamar.user_management_service.domain.dto.res.ResUser
import com.abuamar.user_management_service.domain.dto.res.ResUserById
import com.abuamar.user_management_service.domain.dto.res.ResUserId
import com.abuamar.user_management_service.domain.constant.TopicKafka
import com.abuamar.user_management_service.domain.entity.MasterUserEntity
import com.abuamar.user_management_service.exception.CustomException
import com.abuamar.user_management_service.producer.KafkaProducer
import com.abuamar.user_management_service.repository.MasterUserRepository
import com.abuamar.user_management_service.service.AsyncUserService
import com.abuamar.user_management_service.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class UserServiceImpl(
    private val masterUserRepository: MasterUserRepository,
    private val httpServletRequest: HttpServletRequest,
    private val kafkaProducer: KafkaProducer<Any>,
    private val asyncUserService: AsyncUserService
): UserService {
    // Helper Functions
    private fun getAuthenticatedUserId(): Int {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_ID).toInt()
    }

    private fun isAdmin(): Boolean {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_AUTHORITY) == AppConstants.ROLE_ADMIN
    }

    private fun requireAdmin() {
        if (!isAdmin()) {
            throw CustomException(
                AppConstants.ERR_UNAUTHORIZED,
                HttpStatus.FORBIDDEN.value()
            )
        }
    }
    
    private fun getAuthenticatedUser(): MasterUserEntity {
        val userId = getAuthenticatedUserId()
        return masterUserRepository.findUserActiveById(userId).orElseThrow {
            CustomException(
                "${AppConstants.ERR_USER_NOT_FOUND} with id $userId",
                HttpStatus.NOT_FOUND.value()
            )
        }
    }
    
    override fun findAllUser(): List<ResUser> {
        requireAdmin()

        val rawData = masterUserRepository.findAll()

        return rawData.map { user ->
            ResUser(
                id = user.id,
                username = user.username,
                fullName = user.fullName,
                createdAt = user.createdAt!!,
                createdBy = user.createdBy ?: AppConstants.SYSTEM_USER,
            )
        }
    }

    override fun findUserById(id: Int): ResUserById {
        val authenticatedUserId = getAuthenticatedUserId()
        val isSelf = authenticatedUserId == id

        if (!isSelf && !isAdmin()) {
            throw CustomException(
                AppConstants.ERR_UNAUTHORIZED,
                HttpStatus.FORBIDDEN.value()
            )
        }

        val result = masterUserRepository.findUserActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_USER_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        return ResUserById(
            id = result.id,
            username = result.username,
            fullName = result.fullName,
            roleName = result.role?.name,
            createdAt = result.createdAt!!,
            createdBy = result.createdBy ?: AppConstants.SYSTEM_USER
        )
    }

    @Transactional
    override fun updateUserById(req: ReqUserUpdate): ResUserById {
        val authenticatedUserId = getAuthenticatedUserId()
        val isSelf = authenticatedUserId == req.id

        if (!isSelf && !isAdmin()) {
            throw CustomException(
                AppConstants.ERR_UNAUTHORIZED,
                HttpStatus.FORBIDDEN.value()
            )
        }

        val user = masterUserRepository.findUserActiveById(req.id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_USER_NOT_FOUND} with id ${req.id}",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (user.isDelete) {
            throw CustomException(
                "${AppConstants.ERR_USER_DELETED} with id ${req.id}",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        val updater = getAuthenticatedUser()

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
            createdBy = updatedUser.createdBy ?: AppConstants.SYSTEM_USER
        )
    }

    override fun deleteUserById(id: Int) {
        requireAdmin()

        val user = masterUserRepository.findUserActiveById(id).orElseThrow {
            throw CustomException(
                AppConstants.ERR_USER_NOT_FOUND + " with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        val admin = masterUserRepository.findUserActiveById(getAuthenticatedUserId()).orElseThrow {
            throw CustomException(
                AppConstants.ERR_ADMIN_NOT_FOUND + " with id ${getAuthenticatedUserId()}",
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
        requireAdmin()

        val user = masterUserRepository.findById(id).orElseThrow {
            throw CustomException(
                AppConstants.ERR_USER_NOT_FOUND + " with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (!user.isDelete) {
            throw CustomException(
                AppConstants.ERR_ALREADY_ACTIVE + " with id $id",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        val admin = masterUserRepository.findUserActiveById(getAuthenticatedUserId()).orElseThrow {
            throw CustomException(
                AppConstants.ERR_ADMIN_NOT_FOUND + " with id ${getAuthenticatedUserId()}",
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

    override fun bulkDeleteUsers(userIds: List<Int>) {
        userIds.forEach { userId ->
            asyncUserService.asyncDeleteUsers(userId)
        }
    }
}
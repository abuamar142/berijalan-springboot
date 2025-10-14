package com.abuamar.user_management_service.service

import com.abuamar.user_management_service.domain.dto.req.ReqUserUpdate
import com.abuamar.user_management_service.domain.dto.res.ResUser
import com.abuamar.user_management_service.domain.dto.res.ResUserById
import com.abuamar.user_management_service.domain.dto.res.ResUserId

interface UserService {
    fun findAllUser(): List<ResUser>
    fun findUserById(id: Int): ResUserById
    fun findUserByIdInternal(id: Int): ResUserById  // New: No authentication required
    fun updateUserById(req: ReqUserUpdate): ResUserById
    fun deleteUserById(id: Int)
    fun restoreUserById(id: Int)
    fun getUsersByUniqueIds(userIds: List<Int>): List<ResUserId>
    fun bulkDeleteUsers(userIds: List<Int>)
}
package com.abuamar.user_management_service.service

import com.abuamar.user_management_service.domain.dto.res.ResUserById
import com.abuamar.user_management_service.domain.dto.req.ReqLogin
import com.abuamar.user_management_service.domain.dto.req.ReqRegister
import com.abuamar.user_management_service.domain.dto.res.ResLogin

interface AuthService {
    fun login(req: ReqLogin): ResLogin
    fun register(req: ReqRegister): ResUserById
}
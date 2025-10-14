package com.abuamar.user_management_service.service.impl

import com.abuamar.user_management_service.service.AsyncUserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AsyncUserServiceImpl: AsyncUserService {
    val log = LoggerFactory.getLogger(AsyncUserServiceImpl::class.java)

    override fun asyncDeleteUsers(userId: Int) {
        log.info("Delete users with $userId from thread: ${Thread.currentThread().name}")

        Thread.sleep(2_000)

        log.info("Deleted users with $userId with thread: ${Thread.currentThread().name}")
    }
}
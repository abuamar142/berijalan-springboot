package com.abuamar.user_management_service.service.impl

import com.abuamar.user_management_service.rest.DummyJsonClient
import com.abuamar.user_management_service.service.DummyJsonService
import org.springframework.stereotype.Service

@Service
class DummyJsonServiceImpl(
    private val dummyJsonClient: DummyJsonClient
): DummyJsonService {
    override fun getProducts(): Any {
        return dummyJsonClient.getProducts()
    }
}
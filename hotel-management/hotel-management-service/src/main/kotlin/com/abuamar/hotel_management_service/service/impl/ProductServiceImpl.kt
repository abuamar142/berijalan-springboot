package com.abuamar.hotel_management_service.service.impl

import com.abuamar.hotel_management_service.domain.constant.TopicKafka
import com.abuamar.hotel_management_service.domain.dto.req.ReqProduct
import com.abuamar.hotel_management_service.domain.dto.res.ResProduct
import com.abuamar.hotel_management_service.exception.CustomException
import com.abuamar.hotel_management_service.repository.MasterProductRepository
import com.abuamar.hotel_management_service.rest.UserClient
import com.abuamar.hotel_management_service.service.ProductService
import jakarta.servlet.http.HttpServletRequest
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    val productRepository: MasterProductRepository,
    val userClient: UserClient,
    private val httpServletRequest: HttpServletRequest,
): ProductService {
    @Cacheable(
        "getProducts"
    )
    override fun getProducts(): List<ResProduct> {
        val rawData = productRepository.findAll().ifEmpty {
            throw CustomException(
                "No products found",
                404
            )
        }

        // Get all unique userIds
        val userIds = rawData.distinctBy { it.userId }.map { it.userId }

        // Fetch unique users
        val uniqueUsers = userClient.getUsersByIds(userIds).body!!.data!!

        rawData.forEach { item ->
            ResProduct(
                id = item.id,
                name = item.name,
                price = item.price,
                brandName = item.brand!!.name,
                createdAt = item.createdAt!!,
                createdBy = item.createdBy ?: "",
            )

            val productOwner = uniqueUsers.find { it.userId == item.userId }

            item.createdBy = productOwner?.fullName ?: "Unknown"
        }

        return rawData.map { item ->
            ResProduct(
                id = item.id,
                name = item.name,
                price = item.price,
                brandName = item.brand!!.name,
                createdAt = item.createdAt!!,
                createdBy = item.createdBy ?: "",
            )
        }
    }

    override fun getProductById(id: Int): ResProduct? {
        TODO("Not yet implemented")
    }

    override fun updateProduct(req: ReqProduct): ResProduct {
        val product = productRepository.findById(req.id).orElseThrow {
            throw CustomException(
                "Product with id ${req.id} not found",
                404
            )
        }

        val authority = httpServletRequest.getHeader("X-USER-ID")

        product.updatedBy = authority ?: "Unknown"

        product.name = req.name
        product.price = req.price


        val updatedProduct = productRepository.save(product)

        return ResProduct(
            id = updatedProduct.id,
            name = updatedProduct.name,
            price = updatedProduct.price,
            brandName = updatedProduct.brand!!.name,
            createdAt = updatedProduct.createdAt!!,
            createdBy = updatedProduct.createdBy ?: "",
            updatedBy = updatedProduct.updatedBy ?: "",
        )
    }

    override fun deleteUserProducts(userId: Int) {
        val userProductList = productRepository.findAllByUserId(userId)

        userProductList.forEach { product ->
            product.isDelete = true
            product.updatedBy = httpServletRequest.getHeader("X-USER-ID") ?: "Unknown"
        }

        productRepository.saveAll(userProductList)
    }
}
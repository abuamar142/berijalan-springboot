package com.abuamar.hotel_management_service.consumer

import com.abuamar.hotel_management_service.domain.constant.TopicKafka
import com.abuamar.hotel_management_service.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener

@Configuration
class UserManagementConsumer(
     private val productService: ProductService
) {
    val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        containerFactory = "kafkaListenerContainerFactory",
        id = "ABUAMAR_DELETE_USER_PRODUCT",
        topics = [TopicKafka.DELETE_USER_PRODUCT]
    )

    @KafkaHandler
    fun deleteUserProductHandler(message: String) {
        log.info("Received message: $message from topic: ${TopicKafka.DELETE_USER_PRODUCT}")

        val userId = message.replace("\"\"", "")
        productService.deleteUserProducts(userId.toInt())
    }
}
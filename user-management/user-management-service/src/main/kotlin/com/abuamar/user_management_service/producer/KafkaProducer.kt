package com.abuamar.user_management_service.producer

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service // Business layer
class KafkaProducer<T>(
    private val kafkaTemplate: KafkaTemplate<String, T>
) {
    val log = LoggerFactory.getLogger(this::class.java)

    fun sendMessage(topic: String, message: T) {
        log.info("Sending message: $message of topic: $topic")
        kafkaTemplate.send(topic, message)
    }
}
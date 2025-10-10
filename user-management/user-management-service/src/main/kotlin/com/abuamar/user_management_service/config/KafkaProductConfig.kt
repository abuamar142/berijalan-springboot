package com.abuamar.user_management_service.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProductConfig(
    @param:Value("\${kafka.bootstrap-servers}")
    private val kafkaBootstrapServers: String
) {
    @Bean
    fun kafkaProducerFactory(): ProducerFactory<String, Any> {
        val props = mapOf<String, Any>(
            // Connect to  localhost:9092
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
            ProducerConfig.CLIENT_ID_CONFIG to "ABUAMAR_USER_MANAGEMENT_SERVICE",

            // Serialize the topic
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,

            // Serialize the value or message
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
        )

        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(kafkaProducerFactory())
    }
}
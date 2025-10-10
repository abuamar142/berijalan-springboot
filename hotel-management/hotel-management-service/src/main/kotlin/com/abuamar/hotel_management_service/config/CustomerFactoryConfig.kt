package com.abuamar.hotel_management_service.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import kotlin.Any
import kotlin.String

@Configuration
class CustomerFactoryConfig(
    @param:Value("\${kafka.bootstrap-servers}")
    private val kafkaBootstrapServers: String,
) {
    @Bean
    fun kafkaConsumerFactory(): ConsumerFactory<String, Any> {
        val props = mapOf<String, Any>(
            // Connect to  localhost:9092
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to "ABUAMAR_HOTEL_MANAGEMENT_SERVICE",
        )

        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, Any> = ConcurrentKafkaListenerContainerFactory()

        factory.consumerFactory = kafkaConsumerFactory()

        return factory
    }
}
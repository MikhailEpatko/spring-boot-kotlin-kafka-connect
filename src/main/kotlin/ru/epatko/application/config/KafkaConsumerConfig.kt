package ru.epatko.application.config

import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.CLIENT_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties

@EnableKafka
@Configuration
class KafkaConsumerConfig @Autowired constructor(
    private val env: Environment,
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<Any, Any> {
        val props: MutableMap<String, Any?> = HashMap()
        props[BOOTSTRAP_SERVERS_CONFIG] = env.getProperty("kafka.consumer.bootstrap-servers")
        props[GROUP_ID_CONFIG] = env.getProperty("kafka.consumer.group-id")
        props[CLIENT_ID_CONFIG] = env.getProperty("kafka.consumer.client-id")
        props[KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[VALUE_DESERIALIZER_CLASS_CONFIG] = EventDeserializer::class.java
        props[MAX_POLL_RECORDS_CONFIG] = env.getProperty("kafka.consumer.max-poll-records")
        props[AUTO_OFFSET_RESET_CONFIG] = env.getProperty("kafka.consumer.auto-offset-reset")
        props[SESSION_TIMEOUT_MS_CONFIG] = env.getProperty("kafka.consumer.session.timeout.ms")
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun appUserKafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<Any, Any>
    ): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Any, Any>> {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        factory.consumerFactory = consumerFactory
        factory.containerProperties.pollTimeout = 3000
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.isSyncCommits = true
        factory.setConcurrency(1)
        return factory
    }
}

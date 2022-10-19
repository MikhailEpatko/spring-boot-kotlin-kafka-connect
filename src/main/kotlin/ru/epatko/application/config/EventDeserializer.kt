package ru.epatko.application.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import ru.epatko.application.source.model.KafkaEventDto

class EventDeserializer : Deserializer<KafkaEventDto> {

    private val objectMapper = ObjectMapper()

    override fun deserialize(topic: String?, data: ByteArray?): KafkaEventDto? =
        data?.let { objectMapper.readValue(String(data), KafkaEventDto::class.java) }

    override fun close() = Unit
}

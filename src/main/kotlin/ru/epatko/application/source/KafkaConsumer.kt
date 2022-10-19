package ru.epatko.application.source

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import ru.epatko.application.source.model.KafkaEventDto
import ru.epatko.application.source.model.KafkaEventModel
import ru.epatko.application.source.model.ProjectModel

@Component
class KafkaConsumer {

    private val mapper = ObjectMapper()

    @KafkaListener(
        topics = ["\${kafka.consumer.app-user-topic}"],
        groupId = "\${kafka.consumer.group-id}",
        containerFactory = "appUserKafkaListenerContainerFactory",
    )
    fun listen(
        consumerRecord: ConsumerRecord<Any, KafkaEventDto>,
        ack: Acknowledgment,
    ) = CoroutineScope(Dispatchers.IO).launch {
        val eventDto = consumerRecord.value()
        processEvent(eventDto)
        ack.acknowledge()
    }

    private suspend fun processEvent(eventDto: KafkaEventDto?) = when (eventDto?.source?.table) {
        "app_user" -> println(
            convertToModel(eventDto, ProjectModel::class.java)
        )
        else -> println("Unknown table event")
    }

    private fun <T> convertToModel(eventDto: KafkaEventDto, clazz: Class<T>): KafkaEventModel<T> =
        KafkaEventModel(
            data = mapper.convertValue(eventDto.getData(), clazz),
            op = eventDto.op,
        )
}

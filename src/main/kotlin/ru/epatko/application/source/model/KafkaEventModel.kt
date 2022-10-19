package ru.epatko.application.source.model

data class KafkaEventModel<T>(
    val op: String? = null,
    val data: T,
)

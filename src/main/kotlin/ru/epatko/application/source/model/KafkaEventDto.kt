package ru.epatko.application.source.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KafkaEventDto(

    @JsonProperty("before")
    val before: Any? = null,

    @JsonProperty("after")
    val after: Any? = null,

    @JsonProperty("source")
    val source: Source? = null,

    @JsonProperty("op")
    val op: String? = null,
) {
    fun getData() = after ?: before!!
}

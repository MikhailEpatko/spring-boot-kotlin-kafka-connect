package ru.epatko.application.source.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Source(

    @JsonProperty("connector")
    val connector: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("db")
    val db: String,

    @JsonProperty("table")
    val table: String,
)
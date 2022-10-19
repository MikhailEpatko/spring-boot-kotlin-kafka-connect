package ru.epatko.application.source.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectModel(

    @JsonProperty("id")
    val id: Long,

    @JsonProperty("sync_ps")
    val synchronized: Boolean,
)
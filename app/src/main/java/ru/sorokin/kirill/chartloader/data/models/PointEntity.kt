package ru.sorokin.kirill.chartloader.data.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Модель точки на плоскости
 *
 * @param x координата x
 * @param y координата y
 *
 * @author Sorokin Kirill
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PointEntity @JsonCreator constructor(
    @param:JsonProperty("x")
    @get:JsonProperty("x")
    var x: Float,

    @param:JsonProperty("y")
    @get:JsonProperty("y")
    var y: Float
)

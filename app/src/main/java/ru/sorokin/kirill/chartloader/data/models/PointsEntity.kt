package ru.sorokin.kirill.chartloader.data.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Модель набора точек
 *
 * @param points набор точек
 *
 * @author Sorokin Kirill
 */
data class PointsEntity @JsonCreator constructor(
    @param:JsonProperty("points")
    @get:JsonProperty("points")
    var points: List<PointEntity>
)
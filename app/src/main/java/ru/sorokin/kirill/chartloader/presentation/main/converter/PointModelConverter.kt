package ru.sorokin.kirill.chartloader.presentation.main.converter

import android.graphics.PointF
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * Конвертер сущностей из domain слоя в сущности presentation слоя
 *
 * @author Sorokin Kirill
 */
interface PointModelConverter {
    /**
     * Сконвертировать список моделей [list]
     */
    fun convert(list: List<PointF>): List<PointModel>

}
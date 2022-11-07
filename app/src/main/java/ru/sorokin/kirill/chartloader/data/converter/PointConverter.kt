package ru.sorokin.kirill.chartloader.data.converter

import android.graphics.PointF
import ru.sorokin.kirill.chartloader.data.models.PointsEntity

/**
 * Конвертер моделей
 *
 * @author Sorokin Kirill
 */
interface PointConverter {

    /**
     * Конвертировать модель [entity] из Data слоя в модель Domain слоя
     */
    fun convert(entity: PointsEntity): List<PointF>

}
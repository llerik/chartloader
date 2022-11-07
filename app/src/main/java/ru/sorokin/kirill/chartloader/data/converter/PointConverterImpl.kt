package ru.sorokin.kirill.chartloader.data.converter

import android.graphics.PointF
import ru.sorokin.kirill.chartloader.data.models.PointsEntity

/**
 * Конвертер моделей
 *
 * @author Sorokin Kirill
 */
class PointConverterImpl : PointConverter {

    override fun convert(entity: PointsEntity): List<PointF> {
        return entity.points.map {
            PointF(it.x, it.y)
        }
    }
}
package ru.sorokin.kirill.chartloader.data.converter

import android.graphics.PointF
import ru.sorokin.kirill.chartloader.data.models.PointsEntity

/**
 * todo
 *
 * @author Sorokin Kirill
 */
interface PointConverter {

    fun convert(entity: PointsEntity): List<PointF>

}
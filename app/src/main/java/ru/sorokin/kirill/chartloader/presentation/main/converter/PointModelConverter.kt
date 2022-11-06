package ru.sorokin.kirill.chartloader.presentation.main.converter

import android.graphics.PointF
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * todo
 *
 * @author Sorokin Kirill
 */
interface PointModelConverter {
    /**
     * todo
     */
    fun convert(list: List<PointF>): List<PointModel>

}
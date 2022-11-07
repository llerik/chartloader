package ru.sorokin.kirill.chartloader.presentation.main.converter

import android.graphics.PointF
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class PointModelConverterImpl : PointModelConverter {
    override fun convert(list: List<PointF>): List<PointModel> =
        list.sortedWith(ComparatorPointF())
            .map {
                PointModel(
                    point = it
                )
            }

    private class ComparatorPointF : Comparator<PointF> {
        override fun compare(o1: PointF, o2: PointF): Int {
            return when {
                o1.x > o2.x -> 1
                o1.x < o2.x -> -1
                else -> 0
            }
        }
    }
}
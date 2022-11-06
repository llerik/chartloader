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
            .mapIndexed { i, point ->
                when (i) {
                    0 -> {
                        val next = list[1]
                        PointModel(
                            point = point,
                            offset = PointF(
                                (next.x - point.x) / COEFFICIENT_X,
                                (next.y - point.y) / COEFFICIENT_Y
                            )
                        )
                    }
                    list.lastIndex -> {
                        val prev = list[i - 1]
                        PointModel(
                            point = point,
                            offset = PointF(
                                (point.x - prev.x) / COEFFICIENT_X,
                                (point.y - prev.y) / COEFFICIENT_Y
                            )
                        )
                    }
                    else -> {
                        val next = list[i + 1]
                        val prev = list[i - 1]
                        PointModel(
                            point = point,
                            offset = PointF(
                                (next.x - prev.x) / COEFFICIENT_X,
                                (next.y - prev.y) / COEFFICIENT_Y
                            )
                        )
                    }
                }
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

    companion object {
        /**
         * Коэффициент для расчета опорной точки, для отрисовки кривых
         */
        private const val COEFFICIENT_X = 2
        private const val COEFFICIENT_Y = 4
    }
}
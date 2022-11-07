package ru.sorokin.kirill.chartloader.presentation.main.converter

import android.graphics.PointF
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Конвертер сущностей из domain слоя в сущности presentation слоя
 *
 * @author Sorokin Kirill
 */
class PointModelConverterImpl : PointModelConverter {
    override fun convert(list: List<PointF>): List<PointModel> {
        val sortedList = list.sortedWith(ComparatorPointF())
        return sortedList.mapIndexed { index, point ->
            when (index) {
                0 -> PointModel(
                    point = point,
                    firstNormal = PointF(),
                    secondNormal = PointF()
                )
                else -> {
                    val prev = sortedList[index - 1]
                    val normals = findNormalPoints(prev, point)
                    PointModel(
                        point = point,
                        firstNormal = normals.first,
                        secondNormal = normals.second
                    )
                }
            }
        }
    }

    /**
     * Найти пару нормалей для построения кривой безье
     *
     * @param pointA точка A
     * @param pointB точка B
     */
    private fun findNormalPoints(
        pointA: PointF,
        pointB: PointF
    ): Pair<PointF, PointF> {
        //найдем точку - середину AB
        val pointC = PointF(
            (pointB.x + pointA.x) / 2,
            (pointB.y + pointA.y) / 2
        )
        val direction = pointB.y < pointA.y
        val first = findNormalPointMiddle(pointA, pointC, direction)
        //val first = findNormalPoint(pointA, pointC, direction)
        val second = findNormalPointMiddle(pointC, pointB, !direction)
        //val second = findNormalPoint(pointC, pointB, !direction)
        return first to second
    }

    /**
     * Найти точку нормали отведенную от точки A
     *
     * @param pointA координаты точки А
     * @param pointB координаты точки B
     * @param direction направление нормали
     * @param defaultLength длина нормали по умолчанию
     */
    private fun findNormalPoint(
        pointA: PointF,
        pointB: PointF,
        direction: Boolean = true,
        defaultLength: Float = 0.1f
    ): PointF {
        //Есть точка A(pointA) B(pointB). и есть точка C - точка нормали к А

        //величина отрезка BC равна разнице координат Х, чтобы не слипались
        val diff = abs(pointB.x - pointA.x) * 1.2f
        val lengthBC = if (diff == 0f) defaultLength else diff
        //Найдем длину отрезка AB
        val lengthAB =
            sqrt((pointA.x - pointB.x) * (pointA.x - pointB.x) + (pointA.y - pointB.y) * (pointA.y - pointB.y))
        //найдем единичный вектор AB
        val baV1 = PointF(
            (pointB.x - pointA.x) / lengthAB,
            (pointB.y - pointA.y) / lengthAB
        )
        //повернем вектор в сторону точки C
        val bcV1 = PointF(
            (if (direction) -baV1.y else baV1.y) * lengthBC,
            (if (direction) baV1.x else -baV1.x) * lengthBC
        )
        return PointF(
            pointA.x + bcV1.x,
            pointA.y + bcV1.y
        )
    }

    /**
     * Найти точку нормали отведенную от середины отрезка AD
     *
     * @param pointA координаты точки А
     * @param pointD координаты точки D
     * @param direction направление нормали
     * @param defaultLength длина нормали по умолчанию
     */
    private fun findNormalPointMiddle(
        pointA: PointF,
        pointD: PointF,
        direction: Boolean = true,
        defaultLength: Float = 0.1f
    ): PointF {
        //Есть точка A(pointA) D(point). Предположим, что есть точка B посередине отрезка AD
        //и есть точка C - точка нормали
        //определим координату B
        val pointB = PointF(
            (pointD.x + pointA.x) / 2,
            (pointD.y + pointA.y) / 2
        )
        //величина отрезка BC равна разнице координат Х, чтобы не слипались
        val diff = abs(pointD.x - pointA.x) * 1.2f
        val lengthBC = if (diff == 0f) defaultLength else diff
        //Найдем длину отрезка AB
        val lengthAB =
            sqrt((pointA.x - pointB.x) * (pointA.x - pointB.x) + (pointA.y - pointB.y) * (pointA.y - pointB.y))
        //найдем единичный вектор BA
        val baV1 = PointF(
            (pointA.x - pointB.x) / lengthAB,
            (pointA.y - pointB.y) / lengthAB
        )
        //повернем вектор в сторону точки C
        val bcV1 = PointF(
            (if (direction) -baV1.y else baV1.y) * lengthBC,
            (if (direction) baV1.x else -baV1.x) * lengthBC
        )
        return PointF(
            pointB.x + bcV1.x,
            pointB.y + bcV1.y
        )
    }

    /**
     * Comparator точек для упорядочивания по координате Х
     */
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
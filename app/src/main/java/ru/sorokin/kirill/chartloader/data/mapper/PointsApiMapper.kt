package ru.sorokin.kirill.chartloader.data.mapper

import androidx.annotation.WorkerThread
import ru.sorokin.kirill.chartloader.data.models.PointsEntity

/**
 * todo
 *
 * @author Sorokin Kirill
 */
interface PointsApiMapper {

    @Throws(DataException::class)
    @WorkerThread
    fun getPoints(count: Int): PointsEntity

}
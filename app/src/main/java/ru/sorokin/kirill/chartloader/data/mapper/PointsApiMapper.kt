package ru.sorokin.kirill.chartloader.data.mapper

import androidx.annotation.WorkerThread
import ru.sorokin.kirill.chartloader.data.models.PointsEntity

/**
 * ApiMapper запроса точек
 *
 * @author Sorokin Kirill
 */
interface PointsApiMapper {

    /**
     * Получить модель с набором точек
     *
     * @param count количесто точек
     */
    @Throws(DataException::class)
    @WorkerThread
    fun getPoints(count: Int): PointsEntity

}
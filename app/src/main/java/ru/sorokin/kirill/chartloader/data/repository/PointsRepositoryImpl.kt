package ru.sorokin.kirill.chartloader.data.repository

import android.graphics.PointF
import android.util.Log
import ru.sorokin.kirill.chartloader.data.converter.PointConverter
import ru.sorokin.kirill.chartloader.data.mapper.PointsApiMapper
import ru.sorokin.kirill.chartloader.domain.repository.PointsRepository

/**
 * Репозиторий получения точек
 *
 * @param converter конвертер моделей
 * @param apiMapper apiMapper запроса точек
 *
 * @author Sorokin Kirill
 */
class PointsRepositoryImpl(
    private val converter: PointConverter,
    private val apiMapper: PointsApiMapper
): PointsRepository {
    override fun getPoints(count: Int): List<PointF> =
        runCatching {
            val entity = apiMapper.getPoints(count)
            converter.convert(entity)
        }.getOrElse {
            Log.e(TAG, "getPoints: ", it)
            listOf()
        }

    companion object {
        private const val TAG = "PointsRepositoryImpl"
    }
}
package ru.sorokin.kirill.chartloader.domain.repository

import android.graphics.PointF
import androidx.annotation.WorkerThread

/**
 * Репозиторий получения точек
 *
 * @author Sorokin Kirill
 */
interface PointsRepository {

    /**
     * Получить набор точек в количестве [count]
     */
    @WorkerThread
    fun getPoints(count: Int): List<PointF>

}
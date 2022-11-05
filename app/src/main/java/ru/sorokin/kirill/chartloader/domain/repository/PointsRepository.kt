package ru.sorokin.kirill.chartloader.domain.repository

import android.graphics.PointF
import androidx.annotation.WorkerThread

/**
 * todo
 *
 * @author Sorokin Kirill
 */
interface PointsRepository {

    @WorkerThread
    fun getPoints(count: Int): List<PointF>

}
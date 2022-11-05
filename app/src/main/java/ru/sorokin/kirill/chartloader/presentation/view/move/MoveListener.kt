package ru.sorokin.kirill.chartloader.presentation.view.move

import android.graphics.PointF

/**
 * Интерфейс получения событий перемещения
 *
 * @author Sorokin Kirill
 */
interface MoveListener {

    /**
     * Произошло смещение на [point]
     */
    fun onMove(point: PointF)

}
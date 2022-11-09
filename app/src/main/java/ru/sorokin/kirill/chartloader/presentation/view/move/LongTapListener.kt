package ru.sorokin.kirill.chartloader.presentation.view.move

import android.graphics.PointF

/**
 * Интерфейс долдгого нажатия
 *
 * @author Sorokin Kirill
 */
interface LongTapListener {

    /**
     * Событие долгого нажатия в точку [point]
     */
    fun onLongTap(point: PointF)

}
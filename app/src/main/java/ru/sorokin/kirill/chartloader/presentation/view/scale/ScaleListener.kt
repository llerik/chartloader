package ru.sorokin.kirill.chartloader.presentation.view.scale

/**
 * Интерфейс получения событий изменения размера
 *
 * @author Sorokin Kirill
 */
interface ScaleListener {

    /**
     * Изменился размер на [scaleFactor]
     */
    fun onScaleChange(scaleFactor: Float)

}
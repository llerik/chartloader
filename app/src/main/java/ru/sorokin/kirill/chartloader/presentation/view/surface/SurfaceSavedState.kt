package ru.sorokin.kirill.chartloader.presentation.view.surface

import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * Интерфейс состояния графика
 *
 * @author Sorokin Kirill
 */
interface SurfaceSavedState {
    /**
     * Установить список элементов
     */
    fun setList(list: List<PointModel>)

    /**
     * Получить список элементов
     */
    fun getList(): List<PointModel>

    /**
     * Получить флаг сглаживания
     */
    fun isSmooth(): Boolean

    /**
     * Установить флаг сглаживания
     */
    fun setSmooth(smooth: Boolean)
}
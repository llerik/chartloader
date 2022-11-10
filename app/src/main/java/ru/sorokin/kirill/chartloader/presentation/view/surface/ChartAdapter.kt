package ru.sorokin.kirill.chartloader.presentation.view.surface

import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * Адаптер графика
 *
 * @author Sorokin Kirill
 */
interface ChartAdapter {

    /**
     * Сохранить данные в [state]
     */
    fun save(state: SurfaceSavedState)

    /**
     * Восстановить данные из [state]
     */
    fun load(state: SurfaceSavedState)

    /**
     * Установить список точек [list]
     */
    fun setContent(list: List<PointModel>)

    /**
     * Применить размеры экрана: ширина [w], высота [h]
     */
    fun onSizeChanged(w: Int, h: Int)

    /**
     * Изменен режим сглаживания
     */
    fun switchSmoothMode()

}
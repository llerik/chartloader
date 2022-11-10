package ru.sorokin.kirill.chartloader.presentation.view.surface

import androidx.annotation.WorkerThread
import ru.sorokin.kirill.chartloader.presentation.models.DrawModel

/**
 * Интерфейс репозитория данных
 *
 * @author Sorokin Kirill
 */
interface ChartModelRepository {

    /**
     * Пересчитать и получить модель для отображения
     */
    @WorkerThread
    fun update(): DrawModel

}
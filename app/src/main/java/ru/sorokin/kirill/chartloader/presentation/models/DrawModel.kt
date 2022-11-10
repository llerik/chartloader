package ru.sorokin.kirill.chartloader.presentation.models

import android.graphics.Path

/**
 * Модель для отрисовки
 *
 * @param pathLine путь для отрисовки линий
 * @param pathPoints путь для отрисовки точек
 *
 * @author Sorokin Kirill
 */
data class DrawModel(
    val pathLine: Path,
    val pathPoints: Path
)

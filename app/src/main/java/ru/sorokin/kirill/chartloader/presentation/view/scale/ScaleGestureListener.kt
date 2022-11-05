package ru.sorokin.kirill.chartloader.presentation.view.scale

import android.util.Log
import android.view.ScaleGestureDetector
import kotlin.math.abs

/**
 * Обработчик события изменения размера
 *
 * @param scaleTriggerDistance минимальная величина, считаемая жестом
 * @param scaleListener конечный обаботчик изменения размера
 *
 * @author Sorokin Kirill
 */
class ScaleGestureListener(
    private val scaleTriggerDistance: Float,
    private val scaleListener: ScaleListener
) : ScaleGestureDetector.OnScaleGestureListener {

    private var initialSpan = 0f

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        return if (gestureTolerance(detector)) {
            val scaleFactor = detector.scaleFactor
            if (scaleFactor != 0f) {
                scaleListener.onScaleChange(scaleFactor)
                initialSpan = detector.currentSpan
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        initialSpan = detector.currentSpan
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }

    private fun gestureTolerance(detector: ScaleGestureDetector): Boolean {
        val distanceDelta = abs(initialSpan - detector.currentSpan)
        return distanceDelta > scaleTriggerDistance
    }
}
package ru.sorokin.kirill.chartloader.presentation.view.move

import android.graphics.PointF
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Обработчик жестов
 *
 * @param listener конечный обработчик перемещения
 *
 * @author Sorokin Kirill
 */
class MoveGestureListener(
    private val listener: MoveListener
) : GestureDetector.OnGestureListener {

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        listener.onMove(PointF(-distanceX, -distanceY))
        return false
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}
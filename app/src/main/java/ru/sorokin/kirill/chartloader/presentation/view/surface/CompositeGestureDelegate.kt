package ru.sorokin.kirill.chartloader.presentation.view.surface

import android.content.Context
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import ru.sorokin.kirill.chartloader.presentation.view.move.LongTapListener
import ru.sorokin.kirill.chartloader.presentation.view.move.MoveGestureListener
import ru.sorokin.kirill.chartloader.presentation.view.move.MoveListener
import ru.sorokin.kirill.chartloader.presentation.view.scale.ScaleGestureListener
import ru.sorokin.kirill.chartloader.presentation.view.scale.ScaleListener

/**
 * Делегат обработки жестов
 *
 * @author Sorokin Kirill
 */
class CompositeGestureDelegate(
    context: Context,
    scaleListener : ScaleListener,
    moveListener: MoveListener,
    longTapListener: LongTapListener?,
    private val performListener: () -> Unit
) {

    private val scaleGestureDetector = ScaleGestureDetector(
        context,
        ScaleGestureListener(
            scaleTriggerDistance = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_SCALE_TRIGGER_DISTANCE_IN_DP,
                context.resources.displayMetrics
            ),
            scaleListener = scaleListener
        )
    )
    private val moveGestureDetector = GestureDetector(context,
        MoveGestureListener(moveListener, longTapListener))
    private var downTouch = false

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downTouch = true
            }
            MotionEvent.ACTION_UP -> if (downTouch) {
                downTouch = false
                performListener.invoke()
            }
        }
        scaleGestureDetector.onTouchEvent(event)
        if (!scaleGestureDetector.isInProgress) {
            moveGestureDetector.onTouchEvent(event)
        }
        return true
    }

    companion object {
        /**
         * Минимальный размер (dp) для определения жеста изменения размера
         */
        private const val DEFAULT_SCALE_TRIGGER_DISTANCE_IN_DP = 48f
    }
}
package ru.sorokin.kirill.chartloader.presentation.view

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import ru.sorokin.kirill.chartloader.presentation.view.move.MoveGestureListener
import ru.sorokin.kirill.chartloader.presentation.view.move.MoveListener
import ru.sorokin.kirill.chartloader.presentation.view.scale.ScaleGestureListener
import ru.sorokin.kirill.chartloader.presentation.view.scale.ScaleListener


/**
 * View графика
 *
 * @author Sorokin Kirill
 */
class ChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ScaleListener, MoveListener {

    private val scaleGestureDetector = ScaleGestureDetector(
        context,
        ScaleGestureListener(
            scaleTriggerDistance = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_SCALE_TRIGGER_DISTANCE_IN_DP,
                context.resources.displayMetrics
            ),
            scaleListener = this
        )
    )
    private val moveGestureDetector = GestureDetector(context, MoveGestureListener(this))
    private val distance = PointF(0f, 0f)
    private val points = mutableListOf<PointModel>()
    private val pathLine = Path()
    private val pathPoints = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = DEFAULT_STROKE_WIDTH
        color = Color.BLUE
    }
    private var downTouch = false
    private var isSmooth = false
    private var scale = 1f

    /**
     * Установить флаг сглаживания [isSmooth]
     */
    fun setSmooth(isSmooth: Boolean) {
        this.isSmooth = isSmooth
        Log.d(TAG, "setSmooth: $isSmooth")
        updatePath()
    }

    /**
     * Установить список точек для формирования графика
     * @param list список точек
     * @param isSmooth сглаживание
     */
    fun setContent(list: List<PointModel>, isSmooth: Boolean) {
        Log.d(TAG, "setContent: ")
        points.clear()
        points.addAll(list)
        this.isSmooth = isSmooth
        updatePath()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.scale(scale, scale)
        canvas.drawColor(Color.WHITE)
        canvas.translate(distance.x, distance.y)


        paint.style = Paint.Style.STROKE
        canvas.drawPath(pathLine, paint)

        paint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(pathPoints, paint)
    }

    private fun updatePath() {
        pathLine.reset()
        pathPoints.reset()
        for ((i, point) in points.withIndex()) {
            if (i == 0) {
                pathLine.moveTo(point.point.x, point.point.y)
            } else {
                if (isSmooth) {
                    val prev = points[i - 1]
                    pathLine.cubicTo(
                        prev.point.x + prev.offset.x,
                        prev.point.y + prev.offset.y,
                        point.point.x - point.offset.x,
                        point.point.y - point.offset.y,
                        point.point.x,
                        point.point.y
                    )
                } else {
                    pathLine.lineTo(point.point.x, point.point.y)
                }
            }
            pathPoints.addCircle(point.point.x, point.point.y, RADIUS, Path.Direction.CW)
            if (distance.x > point.point.x) distance.x = point.point.x
            if (distance.y < point.point.y) distance.y = point.point.y
        }
        Log.d(TAG, "setContent: distance: $distance")
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downTouch = true
            }
            MotionEvent.ACTION_UP -> if (downTouch) {
                downTouch = false
                performClick()
            }
        }
        scaleGestureDetector.onTouchEvent(event)
        if (!scaleGestureDetector.isInProgress) {
            moveGestureDetector.onTouchEvent(event)
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onScaleChange(scaleFactor: Float) {
        scale *= scaleFactor
        if (scale < MIN_SCALE) {
            scale = MIN_SCALE
        } else if (scale > MAX_SCALE) {
            scale = MAX_SCALE
        }
        Log.d(TAG, "onScaleChange: scaleFactor: $scaleFactor scale: $scale")
        invalidate()
    }

    override fun onMove(point: PointF) {
        Log.d(TAG, "onMove: $point")
        distance.x += point.x * 1 / scale
        distance.y += point.y * 1 / scale
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable {
        Log.d(TAG, "onSaveInstanceState: ")
        return SavedState(super.onSaveInstanceState()).apply {
            setList(points)
            distance.x = distancePoint.x
            distance.y = distancePoint.y
            scaleFactor = scale
            smooth = isSmooth
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d(TAG, "onRestoreInstanceState: ")
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            with(state) {
                distancePoint = distance
                scale = scaleFactor
                smooth = isSmooth
                updatePath()
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        private const val TAG = "ChartView"

        /**
         * Радиус точки
         */
        private const val RADIUS = 3F

        /**
         * Максимальное увеличение графика
         */
        private const val MAX_SCALE = 40f

        /**
         * Минимальное увеличение (отдаление) графика
         */
        private const val MIN_SCALE = 0.1f

        /**
         * Толщина строки
         */
        private const val DEFAULT_STROKE_WIDTH = 2f

        /**
         * Минимальный размер (dp) для определения жеста изменения размера
         */
        private const val DEFAULT_SCALE_TRIGGER_DISTANCE_IN_DP = 48f
    }

    /**
     * Стейт View
     */
    private class SavedState : BaseSavedState {
        private var points = mutableListOf<PointModel>()
        var scaleFactor = 0f
        var distancePoint = PointF(0f, 0f)
        var smooth = false

        constructor(parcel: Parcel?) : super(parcel) {
            parcel?.apply {
                readList(points, PointModel::class.java.classLoader)
                scaleFactor = readFloat()
                distancePoint = PointF.CREATOR.createFromParcel(this)
                smooth = readInt() != 0
            }
        }

        constructor(parcelable: Parcelable?) : super(parcelable)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeList(points)
            parcel.writeFloat(scaleFactor)
            parcel.writeParcelable(distancePoint, PointF.PARCELABLE_WRITE_RETURN_VALUE)
            parcel.writeInt(if (smooth) 1 else 0)
        }

        fun getList() = points

        fun setList(list: List<PointModel>) {
            points.clear()
            points.addAll(list)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

}
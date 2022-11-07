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
import ru.sorokin.kirill.chartloader.R
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
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
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
    private val distance = PointF()
    private val backgroundColor = attrs?.let {
        val typedArray = context.obtainStyledAttributes(it, R.styleable.ChartView)
        val color = typedArray.getColor(R.styleable.ChartView_chart_background, Color.WHITE)
        typedArray.recycle()
        color
    } ?: Color.WHITE
    private val colorLine = attrs?.let {
        val typedArray = context.obtainStyledAttributes(it, R.styleable.ChartView)
        val color = typedArray.getColor(R.styleable.ChartView_chart_line, Color.GREEN)
        typedArray.recycle()
        color
    } ?: Color.GREEN
    private val points = mutableListOf<PointModel>()
    private val pathLine = Path()
    private val pathPoints = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = DEFAULT_STROKE_WIDTH
        color = colorLine
    }
    private var downTouch = false
    private var scaleRateX = 1f
    private var scaleRateY = 1f

    /**
     * Установить список точек [list] для формирования графика
     */
    fun setContent(list: List<PointModel>) {
        Log.d(TAG, "setContent: $list")
        points.reset(list)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(backgroundColor)
        createPath()
        paint.style = Paint.Style.STROKE
        canvas.drawPath(pathLine, paint)

        paint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(pathPoints, paint)
    }

    private fun createPath() {
        pathLine.reset()
        pathPoints.reset()
        for ((i, model) in points.withIndex()) {
            with(model) {
                if (i == 0) {
                    pathLine.moveTo(point.x, point.y)
                } else {
                    //ломаная линия
                    pathLine.lineTo(point.x, point.y)

                    //плавная линия
//                    pathLine.cubicTo(
//                        firstNormal.x,
//                        firstNormal.y,
//                        secondNormal.x,
//                        secondNormal.y,
//                        point.x,
//                        point.y
//                    )
                }
                pathPoints.addCircle(
                    point.x,
                    point.y,
                    RADIUS,
                    Path.Direction.CW
                )
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scaleByDefault()
        moveByDefault()

        invalidate()
    }

    private fun moveByDefault() {
        if (distance.x == 0f && distance.y == 0f) {
            val chartRect = RectF(0f, 0f, 0f, 0f)
            points.forEach { point ->
                with(point.point) {
                    if (x > chartRect.right) chartRect.right = x
                    if (x < chartRect.left) chartRect.left = x
                    if (y > chartRect.bottom) chartRect.bottom = y
                    if (y < chartRect.top) chartRect.top = y
                }
            }
            val chartHeight = chartRect.bottom - chartRect.top
            val chartWidth = chartRect.right - chartRect.left
            distance.x = -chartRect.left - chartWidth / 2 + width / 2
            distance.y = -chartRect.top - chartHeight / 2 + height / 2
            points.forEach {
                it.move(distance)
            }
            distance.x = 0f
            distance.y = 0f
        }
    }

    private fun scaleByDefault() {
        if (scaleRateX == 1f && scaleRateY == 1f) {
            val chartRect = RectF(0f, 0f, 0f, 0f)
            points.forEach { point ->
                with(point.point) {
                    if (x > chartRect.right) chartRect.right = x
                    if (x < chartRect.left) chartRect.left = x
                    if (y > chartRect.bottom) chartRect.bottom = y
                    if (y < chartRect.top) chartRect.top = y
                }
            }
            val chartHeight = chartRect.bottom - chartRect.top
            val defaultScale = height / chartHeight
            points.forEach {
                it.scale(PointF(defaultScale, defaultScale))
            }
            scaleRateX = 1f
            scaleRateY = 1f
        }
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
        scaleRateX *= scaleFactor
        if (scaleRateX < MIN_SCALE) {
            scaleRateX = MIN_SCALE
        } else if (scaleRateX > MAX_SCALE) {
            scaleRateX = MAX_SCALE
        }
        points.forEach { it.update() }
        scaleRateX = 1f
        invalidate()
    }

    override fun onMove(point: PointF) {
        distance.x += point.x
        distance.y += point.y

        points.forEach { it.update() }
        distance.x = 0f
        distance.y = 0f
        invalidate()
    }

    private fun PointModel.update() {
        scale(PointF(scaleRateX, scaleRateY))
        move(distance)
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            setList(points)
            distancePoint = distance
            scaleFactorX = scaleRateX
            scaleFactorY = scaleRateY
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            with(state) {
                distance.x = distancePoint.x
                distance.y = distancePoint.y
                scaleRateX = scaleFactorX
                scaleRateY = scaleFactorY
                points.reset(getList())
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun <T> MutableList<T>.reset(list: List<T>) {
        clear()
        addAll(list)
    }

    companion object {
        private const val TAG = "ChartView"

        /**
         * Радиус точки
         */
        private const val RADIUS = 5f

        /**
         * Максимальное увеличение графика
         */
        private const val MAX_SCALE = 10000f

        /**
         * Минимальное увеличение (отдаление) графика
         */
        private const val MIN_SCALE = 0.01f

        /**
         * Толщина строки
         */
        private const val DEFAULT_STROKE_WIDTH = 5f

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
        var scaleFactorX = 1f
        var scaleFactorY = 1f
        var distancePoint = PointF()

        constructor(parcel: Parcel?) : super(parcel) {
            parcel?.apply {
                readList(points, PointModel::class.java.classLoader)
                scaleFactorX = readFloat()
                scaleFactorY = readFloat()
                distancePoint = PointF.CREATOR.createFromParcel(this)
            }
        }

        constructor(parcelable: Parcelable?) : super(parcelable)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeList(points)
            parcel.writeFloat(scaleFactorX)
            parcel.writeFloat(scaleFactorY)
            parcel.writeParcelable(distancePoint, PointF.PARCELABLE_WRITE_RETURN_VALUE)
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
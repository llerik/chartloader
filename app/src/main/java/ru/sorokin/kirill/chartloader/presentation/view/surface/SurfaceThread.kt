package ru.sorokin.kirill.chartloader.presentation.view.surface

import android.graphics.*
import android.view.SurfaceHolder
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import ru.sorokin.kirill.chartloader.presentation.view.move.MoveListener
import ru.sorokin.kirill.chartloader.presentation.view.scale.ScaleListener
import ru.sorokin.kirill.chartloader.utils.Logger

/**
 * Thread формирования изображения графика
 *
 * @param surfaceHolder холдер view
 * @param colorLine цвет линии
 * @param backgroundColor цвет фона
 *
 * @author Sorokin Kirill
 */
class SurfaceThread(
    private val surfaceHolder: SurfaceHolder,
    private val colorLine: Int,
    private val backgroundColor: Int
) : Thread(), ScaleListener, MoveListener {

    var isRunning = false
    private var isInitialized = false
    private val lock = Object()

    private val distance = PointF()
    private val points = mutableListOf<PointModel>()
    private val pathLine = Path()
    private val pathPoints = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = DEFAULT_STROKE_WIDTH
        color = colorLine
    }
    private var scaleRate = PointF(1f, 1f)
    private var height: Int = 0
    private var width: Int = 0
    private var isSmoothEnable = false

    override fun run() {
        super.run()
        while (isRunning) {
            var c: Canvas? = null
            try {
                synchronized(lock) {
                    update()
                    c = surfaceHolder.lockCanvas(null)
                    synchronized(surfaceHolder) {
                        c?.draw()
                    }
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c)
                }
            }
        }
    }

    fun switchSmoothMode() {
        synchronized(lock) {
            isSmoothEnable = !isSmoothEnable
            Logger.d(TAG, "switchSmoothMode: $isSmoothEnable")
        }
    }

    fun setContent(list: List<PointModel>) {
        Logger.d(TAG, "setContent: $list")
        synchronized(lock) {
            points.reset(list)
        }
        Logger.d(TAG, "setContent: colorLine: $colorLine backgroundColor: $backgroundColor")
    }

    override fun onScaleChange(scaleFactor: Float) {
        synchronized(lock) {
            Logger.d(TAG, "onScaleChange: $scaleFactor")
            scaleRate.x *= scaleFactor
            if (scaleRate.x < MIN_SCALE) {
                scaleRate.x = MIN_SCALE
            } else if (scaleRate.x > MAX_SCALE) {
                scaleRate.x = MAX_SCALE
            }
            points.forEach { it.update() }
            scaleRate.x = 1f
        }
    }

    override fun onMove(point: PointF) {
        synchronized(lock) {
            Logger.d(TAG, "onMove: $point")
            distance.x += point.x

            points.forEach { it.update() }
            distance.x = 0f
        }
    }

    fun onSizeChanged(w: Int, h: Int) {
        synchronized(lock) {
            Logger.d(TAG, "onSizeChanged: w: $w h: $h")
            height = h
            width = w
            if (!isInitialized) {
                isInitialized = true
                scaleByDefault()
                moveByDefault()
            }
        }
    }

    fun save(state: SurfaceSavedState) {
        synchronized(lock) {
            state.setList(points)
            state.setSmooth(isSmoothEnable)
        }
    }

    fun load(state: SurfaceSavedState) {
        synchronized(lock) {
            points.reset(state.getList())
            isSmoothEnable = state.isSmooth()
            isInitialized = true
        }
    }

    private fun Canvas.draw() {
        drawColor(backgroundColor)
        paint.style = Paint.Style.STROKE
        drawPath(pathLine, paint)

        paint.style = Paint.Style.FILL_AND_STROKE
        drawPath(pathPoints, paint)
    }

    private fun update() {
        pathLine.reset()
        pathPoints.reset()
        for ((i, model) in points.withIndex()) {
            with(model) {
                if (i == 0) {
                    pathLine.moveTo(point.x, point.y)
                } else {
                    if (isSmoothEnable) {
                        //плавная линия
                        pathLine.cubicTo(
                            firstNormal.x,
                            firstNormal.y,
                            secondNormal.x,
                            secondNormal.y,
                            point.x,
                            point.y
                        )
                    } else {
                        //ломаная линия
                        pathLine.lineTo(point.x, point.y)
                    }
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

    private fun moveByDefault() {
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

    private fun scaleByDefault() {
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
        scaleRate.x = 1f
        scaleRate.y = 1f

    }

    private fun PointModel.update() {
        scale(scaleRate)
        move(distance)
    }

    private fun <T> MutableList<T>.reset(list: List<T>) {
        clear()
        addAll(list)
    }

    companion object {
        private const val TAG = "SurfaceThread"

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
    }
}
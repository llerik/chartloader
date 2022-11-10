package ru.sorokin.kirill.chartloader.presentation.view.surface

import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import ru.sorokin.kirill.chartloader.presentation.models.DrawModel

/**
 * Thread формирования изображения графика
 *
 * @param surfaceHolder холдер view
 * @param chartModelRepository репозиторий данных
 * @param colorLine цвет линии
 * @param backgroundColor цвет фона
 *
 * @author Sorokin Kirill
 */
class ChartSurfaceThread(
    private val surfaceHolder: SurfaceHolder,
    private val chartModelRepository: ChartModelRepository,
    private val colorLine: Int,
    private val backgroundColor: Int
) : Thread() {

    var isRunning = false

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = DEFAULT_STROKE_WIDTH
        color = colorLine
    }

    override fun run() {
        super.run()
        while (isRunning) {
            var c: Canvas? = null
            try {
                chartModelRepository.update().let { model ->
                    c = surfaceHolder.lockCanvas(null)
                    synchronized(surfaceHolder) {
                        c?.draw(model)
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

    private fun Canvas.draw(model: DrawModel) {
        drawColor(backgroundColor)
        paint.style = Paint.Style.STROKE
        drawPath(model.pathLine, paint)

        paint.style = Paint.Style.FILL_AND_STROKE
        drawPath(model.pathPoints, paint)
    }

    companion object {
        private const val TAG = "SurfaceThread"

        /**
         * Толщина строки
         */
        private const val DEFAULT_STROKE_WIDTH = 5f
    }
}
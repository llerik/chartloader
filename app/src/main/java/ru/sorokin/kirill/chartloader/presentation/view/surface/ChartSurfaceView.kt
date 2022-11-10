package ru.sorokin.kirill.chartloader.presentation.view.surface

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import ru.sorokin.kirill.chartloader.utils.Logger

/**
 * View графика
 *
 * @author Sorokin Kirill
 */
class ChartSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val adapter = ChartAdapterImpl()
    private val delegate = CompositeGestureDelegate(
        context, adapter, adapter, null, this::performClick
    )
    private var thread: ChartSurfaceThread? = null

    private val backgroundColor = attrs?.let {
        val typedArray = context.obtainStyledAttributes(it, R.styleable.ChartSurfaceView)
        val color = typedArray.getColor(R.styleable.ChartSurfaceView_chart_surface_background, Color.WHITE)
        typedArray.recycle()
        color
    } ?: Color.WHITE
    private val colorLine = attrs?.let {
        val typedArray = context.obtainStyledAttributes(it, R.styleable.ChartSurfaceView)
        val color = typedArray.getColor(R.styleable.ChartSurfaceView_chart_surface_line, Color.GREEN)
        typedArray.recycle()
        color
    } ?: Color.GREEN


    init {
        holder.addCallback(this)
    }

    fun switchSmoothMode() {
        adapter.switchSmoothMode()
    }

    fun setContent(list: List<PointModel>) {
        adapter.setContent(list)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        adapter.onSizeChanged(width, height)
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            Logger.d(TAG, "onSaveInstanceState: $this")
            adapter.save(this)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            Logger.d(TAG, "onRestoreInstanceState: $state")
            adapter.load(state)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = ChartSurfaceThread(
            holder,
            adapter,
            colorLine,
            backgroundColor
        ).apply {
            isRunning = true
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        adapter.onSizeChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread?.apply {
            isRunning = false
            var retry = true
            while (retry) {
                try {
                    join()
                    retry = false
                } catch (e: InterruptedException) {
                    Logger.e(TAG, "surfaceDestroyed: ", e)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return delegate.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    companion object {
        private const val TAG = "ChartSurfaceVIew"
    }

    /**
     * Стейт View
     */
    private class SavedState : BaseSavedState, SurfaceSavedState {
        private var points = mutableListOf<PointModel>()
        private var isSmooth = false

        constructor(parcel: Parcel?) : super(parcel) {
            parcel?.apply {
                readList(points, PointModel::class.java.classLoader)
                isSmooth = readInt() != 0
            }
        }

        constructor(parcelable: Parcelable?) : super(parcelable)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeList(points)
            parcel.writeInt(if (isSmooth) 1 else 0)
        }

        override fun setList(list: List<PointModel>) {
            points.clear()
            points.addAll(list)
        }

        override fun getList() = points

        override fun isSmooth() = isSmooth

        override fun setSmooth(smooth: Boolean) {
            isSmooth = smooth
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
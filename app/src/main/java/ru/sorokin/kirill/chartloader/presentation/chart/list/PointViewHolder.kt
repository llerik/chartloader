package ru.sorokin.kirill.chartloader.presentation.chart.list

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class PointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val coordinateX = itemView.findViewById<TextView>(R.id.coordinate_x)
    private val coordinateY = itemView.findViewById<TextView>(R.id.coordinate_y)

    /**
     * todo
     */
    fun bindModel(model: PointModel) {
        with(model.point) {
            Log.d(TAG, "bindModel: $model")
            coordinateX.text = x.toString()
            coordinateY.text = y.toString()
        }
    }

    companion object {
        private const val TAG = "PointViewHolder"
    }

}
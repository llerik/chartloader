package ru.sorokin.kirill.chartloader.presentation.chart.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * ViewHolder столбца таблицы
 *
 * @param itemView view элемента
 *
 * @author Sorokin Kirill
 */
class PointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val coordinateX = itemView.findViewById<TextView>(R.id.coordinate_x)
    private val coordinateY = itemView.findViewById<TextView>(R.id.coordinate_y)

    /**
     * Установить данные из модели [model]
     */
    fun bindModel(model: PointModel) {
        with(model.point) {
            coordinateX.text = x.toString()
            coordinateY.text = y.toString()
        }
    }
}
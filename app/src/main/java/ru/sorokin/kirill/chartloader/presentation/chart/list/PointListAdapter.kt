package ru.sorokin.kirill.chartloader.presentation.chart.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * Адаптер списка точек для таблицы
 *
 * @param items список моделей точек
 *
 * @author Sorokin Kirill
 */
class PointListAdapter(
    private val items: List<PointModel>
): RecyclerView.Adapter<PointViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PointViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.point_view_holder_layout, parent, false)
        )

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bindModel(items[position])
    }

    override fun getItemCount() = items.size

}
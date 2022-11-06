package ru.sorokin.kirill.chartloader.presentation.chart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import ru.sorokin.kirill.chartloader.presentation.view.ChartView

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class ChartActivity:  AppCompatActivity(R.layout.chart_activity) {

    private lateinit var chartView: ChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        chartView = findViewById(R.id.chart_view)

        val parcelablesArray = intent.getParcelableArrayExtra(ARG_POINTS) ?: arrayOf()
        val models = parcelablesArray.asList().map { it as PointModel }
        Log.d(TAG, "onCreate: $models")
        chartView.setContent(models, false)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val TAG = "ChartActivity"
        private const val ARG_POINTS = "points"

        /**
         * todo
         *
         * @param context
         * @param points
         */
        fun newIntent(context: Context, points: List<PointModel>): Intent =
            Intent(context, ChartActivity::class.java).apply {
                val array = points.toTypedArray()
                putExtra(ARG_POINTS, array)
            }
    }
}
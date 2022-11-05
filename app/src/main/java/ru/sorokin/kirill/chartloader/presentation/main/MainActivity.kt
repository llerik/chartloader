package ru.sorokin.kirill.chartloader.presentation.main

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.data.converter.PointConverterImpl
import ru.sorokin.kirill.chartloader.data.mapper.PointsApiMapper
import ru.sorokin.kirill.chartloader.data.mapper.PointsApiMapperImpl
import ru.sorokin.kirill.chartloader.data.parser.JacksonParserImpl
import ru.sorokin.kirill.chartloader.data.parser.Parser
import ru.sorokin.kirill.chartloader.data.repository.PointsRepositoryImpl
import ru.sorokin.kirill.chartloader.domain.repository.PointsRepository
import ru.sorokin.kirill.chartloader.presentation.chart.ChartActivity
import ru.sorokin.kirill.chartloader.presentation.converter.PointModelConverter
import ru.sorokin.kirill.chartloader.presentation.converter.PointModelConverterImpl
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import ru.sorokin.kirill.chartloader.presentation.network.RxSupport
import ru.sorokin.kirill.chartloader.presentation.network.RxSupportImpl

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class MainActivity : FragmentActivity(R.layout.main_activity) {
    private lateinit var viewModel: MainViewModel
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, factory())[MainViewModel::class.java]
        viewModel.getProgressLiveData().observe(this, this::updateProgressBar)
        viewModel.getErrorLiveData().observe(this, this::onError)
        viewModel.getDataLiveData().observe(this, this::showChart)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            viewModel.requestPoints(100)//todo
        }
    }

    private fun showChart(points: List<PointModel>) {
        ChartActivity.newIntent(this, points)
            .let(this::startActivity)
    }

    private fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateProgressBar(visible: Boolean) {

    }

    private fun factory(): ViewModelProvider.Factory {
        //todo move to dagger
        val repository = PointsRepositoryImpl(
            PointConverterImpl(),
            PointsApiMapperImpl(
                JacksonParserImpl(),
                OkHttpClient.Builder().build()
            )
        )
        val converter = PointModelConverterImpl()
        val rxSupport = RxSupportImpl()
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(repository, converter, rxSupport) as T
                }
                throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
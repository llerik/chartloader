package ru.sorokin.kirill.chartloader.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.OkHttpClient
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.data.converter.PointConverterImpl
import ru.sorokin.kirill.chartloader.data.mapper.PointsApiMapperImpl
import ru.sorokin.kirill.chartloader.data.parser.JacksonParserImpl
import ru.sorokin.kirill.chartloader.data.repository.PointsRepositoryImpl
import ru.sorokin.kirill.chartloader.presentation.chart.ChartActivity
import ru.sorokin.kirill.chartloader.presentation.core.network.RxSupportImpl
import ru.sorokin.kirill.chartloader.presentation.core.resource.ResourceManagerImpl
import ru.sorokin.kirill.chartloader.presentation.core.viewmodel.ViewModelProviderFactory
import ru.sorokin.kirill.chartloader.presentation.main.converter.PointModelConverterImpl
import ru.sorokin.kirill.chartloader.presentation.models.PointModel


/**
 * todo
 *
 * @author Sorokin Kirill
 */
class MainActivity : AppCompatActivity(R.layout.main_activity) {
    private lateinit var viewModel: MainViewModel
    private lateinit var button: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var contantLayout: ViewGroup
    private lateinit var textField: TextInputLayout
    private lateinit var textInputEditor: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, factory())[MainViewModel::class.java]
        viewModel.getProgressLiveData().observe(this, this::updateProgressBar)
        viewModel.getErrorLiveData().observe(this, this::onError)
        viewModel.getDataLiveData().observe(this, this::showChart)
        viewModel.getButtonEnableLiveData().observe(this) { button.isEnabled = it }

        button = findViewById(R.id.button)
        progressBar = findViewById(R.id.progress_bar)
        contantLayout = findViewById(R.id.content_layout)
        textField = findViewById(R.id.text_field)
        textInputEditor = findViewById(R.id.text_input_editor)

        textInputEditor.addTextChangedListener {
            if (textField.isErrorEnabled) {
                textField.isErrorEnabled = false
            }
        }
        button.setOnClickListener {
            textField.isErrorEnabled = false
            hideKeyboard()
            viewModel.buttonClick(textInputEditor.text.toString())
        }
    }

    private fun showChart(points: List<PointModel>) {
        ChartActivity.newIntent(this, points)
            .let(this::startActivity)
    }

    private fun onError(message: String) {
        Log.d(TAG, "onError: $message")
        textField.error = message
        textField.isErrorEnabled = true
    }

    private fun updateProgressBar(visible: Boolean) {
        progressBar.visibility = visible.toVisibility()
        contantLayout.visibility = (!visible).toVisibility()
    }

    private fun Boolean.toVisibility() = if (this) View.VISIBLE else View.GONE

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun factory() = ViewModelProviderFactory<MainViewModel> {
        //can move to dagger
        val repository = PointsRepositoryImpl(
            PointConverterImpl(),
            PointsApiMapperImpl(
                JacksonParserImpl(),
                OkHttpClient.Builder().build()
            )
        )
        val converter = PointModelConverterImpl()
        val rxSupport = RxSupportImpl()
        val resourceManager = ResourceManagerImpl(applicationContext)
        MainViewModel(repository, converter, resourceManager, rxSupport)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
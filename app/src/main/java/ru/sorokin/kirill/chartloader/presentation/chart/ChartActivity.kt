package ru.sorokin.kirill.chartloader.presentation.chart

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.presentation.chart.list.PointListAdapter
import ru.sorokin.kirill.chartloader.presentation.core.resource.ResourceManagerImpl
import ru.sorokin.kirill.chartloader.presentation.core.viewmodel.ViewModelProviderFactory
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import ru.sorokin.kirill.chartloader.presentation.models.SuccessSaveImageModel
import ru.sorokin.kirill.chartloader.presentation.view.ChartView
import ru.sorokin.kirill.chartloader.utils.Logger

/**
 * Экран графика
 *
 * @author Sorokin Kirill
 */
class ChartActivity : AppCompatActivity(R.layout.chart_activity) {
    private lateinit var viewModel: ChartViewModel
    private lateinit var chartView: ChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel = ViewModelProvider(viewModelStore, factory())[ChartViewModel::class.java]
        viewModel.getErrorLiveData().observe(this, ::onError)
        viewModel.getSuccessLiveData().observe(this, ::onSuccess)

        val parcelablesArray = intent.getParcelableArrayExtra(ARG_POINTS) ?: arrayOf()
        val models = parcelablesArray.asList().map { it as PointModel }
        Logger.d(TAG, "onCreate: $models")

        chartView = findViewById(R.id.chart_view)
        findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            adapter = PointListAdapter(models)
        }
        chartView.setContent(models)

        //эксперементальный функционал
        //"найдёт лишь тот, кто знает, где он" (с)
        findViewById<View>(R.id.header_table).setOnClickListener { chartView.switchSmoothMode() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                tryToSave()
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                viewModel.saveToFile(getBitmapFromView(chartView))
            } else {
                Log.d(TAG, "onRequestPermissionsResult: permission denied")
                Toast.makeText(this, R.string.save_image_error_permission, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun tryToSave() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "tryToSave: no permission")
            requestPermissions(arrayOf(permission), REQUEST_ID)
        } else {
            viewModel.saveToFile(getBitmapFromView(chartView))
        }
    }

    private fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun onSuccess(model: SuccessSaveImageModel) {
        Log.d(TAG, "onSuccess: $model")
        Snackbar
            .make(chartView, model.message, Snackbar.LENGTH_LONG)
            .setAction(model.buttonName) {
                Intent.createChooser(
                    Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.parse(model.path), "image/*"),
                    getString(R.string.show_image)
                ).let {
                    if (it.resolveActivity(packageManager) != null) {
                        startActivity(it)
                    } else {
                        Logger.w(TAG, "activity not resolved: ")
                        Toast.makeText(
                            this,
                            R.string.error_show_image,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .show()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun factory() = ViewModelProviderFactory<ChartViewModel> {
        ChartViewModel(
            ResourceManagerImpl(applicationContext)
        )
    }

    companion object {
        private const val TAG = "ChartActivity"
        private const val ARG_POINTS = "points"
        private const val REQUEST_ID = 111

        /**
         * Получить [Intent] для запуска экрана с графиком
         *
         * @param context контекст экрана
         * @param points список моделей точек
         */
        fun newIntent(context: Context, points: List<PointModel>): Intent =
            Intent(context, ChartActivity::class.java).apply {
                val array = points.toTypedArray()
                putExtra(ARG_POINTS, array)
            }
    }
}
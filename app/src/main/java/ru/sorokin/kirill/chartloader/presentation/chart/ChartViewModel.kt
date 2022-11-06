package ru.sorokin.kirill.chartloader.presentation.chart

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.presentation.core.SingleLiveEvent
import ru.sorokin.kirill.chartloader.presentation.core.resource.ResourceManager
import ru.sorokin.kirill.chartloader.presentation.core.viewmodel.CoreViewModel
import ru.sorokin.kirill.chartloader.presentation.models.SuccessSaveImageModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class ChartViewModel(
    private val resourceManager: ResourceManager
) : CoreViewModel() {

    private val successLiveData = SingleLiveEvent<SuccessSaveImageModel>()
    private val errorLiveData = SingleLiveEvent<String>()

    /**
     * [LiveData] todo
     */
    fun getSuccessLiveData(): LiveData<SuccessSaveImageModel> = successLiveData

    /**
     * [LiveData] todo
     */
    fun getErrorLiveData(): LiveData<String> = errorLiveData

    fun saveToFile(bitmap: Bitmap) {
        var fOut: OutputStream? = null
        try {
            val selectedOutputPath = makeFileName()
            val file = File(selectedOutputPath)
            fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            successLiveData.value = SuccessSaveImageModel(
                resourceManager.getString(R.string.save_image_success, selectedOutputPath),
                resourceManager.getString(R.string.show),
                selectedOutputPath
            )
        } catch (e: Exception) {
            errorLiveData.value = resourceManager.getString(R.string.error_message_something_went_wrong)
            Log.e(TAG, "saveImage: ", e)
        } finally {
            fOut?.let {
                try {
                    fOut.close()
                } catch (e: IOException) {
                    Log.e(TAG, "saveImage: ", e)
                }
            }
        }
    }

    private fun makeFileName(): String {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            FOLDER_NAME
        )
        if (!mediaStorageDir.exists() || !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "Failed to create directory")
            throw IOException()
        }
        val timeStamp = SimpleDateFormat(DATA_PATTERN, Locale.getDefault())
            .format(Date())
        val imageName = "IMG_$timeStamp.jpg"
        val selectedOutputPath = mediaStorageDir.path + File.separator + imageName
        Log.d(TAG, "selected camera path $selectedOutputPath")
        return selectedOutputPath
    }

    companion object {
        private const val TAG = "ChartViewModel"
        private const val DATA_PATTERN = "yyyyMMdd_HHmmss"
        private const val FOLDER_NAME = "chart"
    }
}
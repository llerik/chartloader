package ru.sorokin.kirill.chartloader.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import ru.sorokin.kirill.chartloader.R
import ru.sorokin.kirill.chartloader.domain.repository.PointsRepository
import ru.sorokin.kirill.chartloader.presentation.core.SingleLiveEvent
import ru.sorokin.kirill.chartloader.presentation.core.network.RxSupport
import ru.sorokin.kirill.chartloader.presentation.core.resource.ResourceManager
import ru.sorokin.kirill.chartloader.presentation.core.viewmodel.CoreViewModel
import ru.sorokin.kirill.chartloader.presentation.main.converter.PointModelConverter
import ru.sorokin.kirill.chartloader.presentation.models.PointModel

/**
 * ViewModel главного экрана
 *
 * @param repository Репозиторий получения точек
 * @param converter Конвертер сущностей из domain слоя в сущности presentation слоя
 * @param resourceManager Менеджер ресурсов
 * @param rxSupport Планировщик потоков
 *
 * @author Sorokin Kirill
 */
class MainViewModel(
    private val repository: PointsRepository,
    private val converter: PointModelConverter,
    private val resourceManager: ResourceManager,
    private val rxSupport: RxSupport
) : CoreViewModel() {

    private val errorLiveData = MutableLiveData<String>()
    private val progressLiveData = MutableLiveData<Boolean>()
    private val buttonEnableLiveData = MutableLiveData<Boolean>()
    private val dataLiveData = SingleLiveEvent<List<PointModel>>()

    /**
     * [LiveData] получения ошибки
     */
    fun getErrorLiveData(): LiveData<String> = errorLiveData

    /**
     * [LiveData] отображения прогресса
     */
    fun getProgressLiveData(): LiveData<Boolean> = progressLiveData

    /**
     * [LiveData] доступности кнопки для нажатия
     */
    fun getButtonEnableLiveData(): LiveData<Boolean> = buttonEnableLiveData

    /**
     * [LiveData] получения данных
     */
    fun getDataLiveData(): LiveData<List<PointModel>> = dataLiveData

    /**
     * Нажата кнопка, когда в поле ввода введен текст [text]
     */
    fun buttonClick(text: String) {
        buttonEnableLiveData.value = false
        if (text.isBlank()) {
            errorLiveData.value = resourceManager.getString(R.string.error_message_input_empty)
            buttonEnableLiveData.value = true
        } else {
            val count = text.toIntOrNull()
            if (count == null || count <= 1) {
                errorLiveData.value =
                    resourceManager.getString(R.string.error_message_incorrect_number)
                buttonEnableLiveData.value = true
            } else {
                requestPoints(count)
            }
        }
    }

    private fun requestPoints(count: Int) {
        progressLiveData.value = true
        rxDisposables.add(
            Single.fromCallable { repository.getPoints(count) }
                .map(converter::convert)
                .subscribeOn(rxSupport.getIOScheduler())
                .observeOn(rxSupport.getMainScheduler())
                .subscribe(
                    {
                        if (it.isEmpty()) {
                            errorLiveData.value =
                                resourceManager.getString(R.string.error_message_something_went_wrong)
                        } else {
                            Log.d(TAG, "requestPoints: $it")
                            dataLiveData.value = it
                        }
                        progressLiveData.value = false
                        buttonEnableLiveData.value = true
                    },
                    {
                        Log.e(TAG, "requestPoints: ", it)
                        progressLiveData.value = false
                        errorLiveData.value =
                            resourceManager.getString(R.string.error_message_something_went_wrong)
                        buttonEnableLiveData.value = true
                    }
                )
        )
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
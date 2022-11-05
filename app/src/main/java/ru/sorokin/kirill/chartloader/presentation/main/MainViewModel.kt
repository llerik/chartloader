package ru.sorokin.kirill.chartloader.presentation.main

import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.schedulers.Schedulers
import ru.sorokin.kirill.chartloader.domain.repository.PointsRepository
import ru.sorokin.kirill.chartloader.presentation.converter.PointModelConverter
import ru.sorokin.kirill.chartloader.presentation.models.PointModel
import ru.sorokin.kirill.chartloader.presentation.network.RxSupport

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class MainViewModel(
    private val repository: PointsRepository,
    private val converter: PointModelConverter,
    private val rxSupport: RxSupport
): ViewModel() {
    private val rxDisposables = CompositeDisposable()
    private val errorLiveData = MutableLiveData<String>()
    private val progressLiveData = MutableLiveData<Boolean>()
    private val dataLiveData = MutableLiveData<List<PointModel>>()

    /**
     * [LiveData] todo
     */
    fun getErrorLiveData(): LiveData<String> = errorLiveData

    /**
     * [LiveData] todo
     */
    fun getProgressLiveData(): LiveData<Boolean> = progressLiveData

    /**
     * [LiveData] todo
     */
    fun getDataLiveData(): LiveData<List<PointModel>> = dataLiveData

    fun requestPoints(count: Int) {
        progressLiveData.value = true
        rxDisposables.add(
            Single.fromCallable { repository.getPoints(count) }
                .map(converter::convert)
                .subscribeOn(rxSupport.getIOScheduler())
                .observeOn(rxSupport.getMainScheduler())
                .subscribe(
                    {
                        if (it.isEmpty()) {
                            errorLiveData.value = "No data"
                        } else {
                            Log.d(TAG, "requestPoints: $it")
                            dataLiveData.value = it
                        }
                        progressLiveData.value = false
                    },
                    {
                        Log.e(TAG, "requestPoints: ", it)
                        progressLiveData.value = false
                        errorLiveData.value = "Ooops!"
                    }
                )
        )
    }

    override fun onCleared() {
        rxDisposables.dispose()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}
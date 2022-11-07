package ru.sorokin.kirill.chartloader.presentation.core.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Базовая ViewModel с поддержкой сохранения и очистки Rx Disposables
 *
 * @author Sorokin Kirill
 */
open class CoreViewModel: ViewModel() {

    protected val rxDisposables = CompositeDisposable()

    override fun onCleared() {
        rxDisposables.dispose()
    }

}
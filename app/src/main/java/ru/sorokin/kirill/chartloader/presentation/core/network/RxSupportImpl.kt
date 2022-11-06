package ru.sorokin.kirill.chartloader.presentation.core.network

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class RxSupportImpl: RxSupport {
    override fun getMainScheduler() = AndroidSchedulers.mainThread()

    override fun getIOScheduler() = Schedulers.io()
}
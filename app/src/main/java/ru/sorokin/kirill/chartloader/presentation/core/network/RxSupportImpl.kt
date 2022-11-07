package ru.sorokin.kirill.chartloader.presentation.core.network

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Обертка над планировщиками потоков
 *
 * @author Sorokin Kirill
 */
class RxSupportImpl: RxSupport {

    override fun getMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

    override fun getIOScheduler() = Schedulers.io()
}
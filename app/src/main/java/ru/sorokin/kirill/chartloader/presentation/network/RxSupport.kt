package ru.sorokin.kirill.chartloader.presentation.network

import io.reactivex.Scheduler

/**
 * todo
 *
 * @author Sorokin Kirill
 */
interface RxSupport {

   fun getMainScheduler(): Scheduler
   fun getIOScheduler(): Scheduler

}
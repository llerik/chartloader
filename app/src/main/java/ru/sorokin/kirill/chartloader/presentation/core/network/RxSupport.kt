package ru.sorokin.kirill.chartloader.presentation.core.network

import io.reactivex.Scheduler

/**
 * Планировщик потоков
 *
 * @author Sorokin Kirill
 */
interface RxSupport {
   /**
    * Получить планировщик Main потока
    */
   fun getMainScheduler(): Scheduler

   /**
    * Получить планировщик IO потоков
    */
   fun getIOScheduler(): Scheduler

}
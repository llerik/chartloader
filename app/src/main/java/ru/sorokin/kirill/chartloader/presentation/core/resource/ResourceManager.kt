package ru.sorokin.kirill.chartloader.presentation.core.resource

import androidx.annotation.StringRes

/**
 * Менеджер ресурсов
 *
 * @author Sorokin Kirill
 */
interface ResourceManager {

    /**
     * Получить строковый ресурс по [id]
     */
    fun getString(@StringRes id: Int): String

    /**
     * Получить строковый ресурс по [id] с параметром [arg]
     */
    fun getString(@StringRes id: Int, arg: String): String

}
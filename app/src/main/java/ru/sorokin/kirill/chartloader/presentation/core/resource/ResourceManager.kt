package ru.sorokin.kirill.chartloader.presentation.core.resource

import androidx.annotation.StringRes

/**
 * todo
 *
 * @author Sorokin Kirill
 */
interface ResourceManager {

    /**
     * todo
     */
    fun getString(@StringRes id: Int): String

}
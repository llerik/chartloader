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

    /**
     * todo
     */
    fun getString(@StringRes id: Int, vararg args: Any): String

}
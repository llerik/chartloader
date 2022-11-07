package ru.sorokin.kirill.chartloader.presentation.core.resource

import android.content.Context

/**
 * Менеджер ресурсов
 *
 * @param context контекст приложения
 *
 * @author Sorokin Kirill
 */
class ResourceManagerImpl(
    private val context: Context
): ResourceManager {

    override fun getString(id: Int): String {
        return context.resources.getString(id)
    }

    override fun getString(id: Int, vararg args: Any): String {
        return context.resources.getString(id, args)
    }
}
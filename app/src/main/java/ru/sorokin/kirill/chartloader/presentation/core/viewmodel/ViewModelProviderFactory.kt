package ru.sorokin.kirill.chartloader.presentation.core.viewmodel

import androidx.core.util.Supplier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * todo
 *
 * @author Sorokin Kirill
 */
class ViewModelProviderFactory<VM : ViewModel>(
    private val supplier: Supplier<VM>
) : ViewModelProvider.Factory {

    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        @Suppress("UNCHECKED_CAST")
        return supplier.get() as VM
    }

}
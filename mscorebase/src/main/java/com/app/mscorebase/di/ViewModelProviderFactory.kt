package com.app.mscorebase.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * Класс создает или возвращает ранее созданную ViewModel
 *
 */
@Suppress("UNCHECKED_CAST")
class ViewModelProviderFactory
@Inject constructor(
    private val viewModelProviderMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelProvider = viewModelProviderMap.get(modelClass)
            ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        return viewModelProvider.get() as T
    }
}
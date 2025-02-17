package com.synngate.twowaysync.ui.screens.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.domain.interactors.impl.CheckServerAvailabilityInteractorImpl


class CheckServerAvailabilityInteractorFactory(
    private val context: Context // <---- Контекст может понадобиться фабрикам ViewModel, даже если интерактору он не нужен напрямую (на всякий случай передаем)
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckServerAvailabilityInteractorImpl::class.java)) { // <---- Фабрика для CheckServerAvailabilityInteractorImpl
            @Suppress("UNCHECKED_CAST")
            return CheckServerAvailabilityInteractorImpl() as T // <---- Создаем CheckServerAvailabilityInteractorImpl без зависимостей
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
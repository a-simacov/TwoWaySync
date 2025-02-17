package com.synngate.twowaysync.ui.screens.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.domain.interactors.AuthenticateServerInteractor
import com.synngate.twowaysync.domain.interactors.impl.AuthenticateServerInteractorImpl


class AuthenticateServerInteractorFactory(
    private val context: Context // <---- Контекст может понадобиться фабрикам ViewModel, даже если интерактору он не нужен напрямую
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthenticateServerInteractorImpl::class.java)) { // <---- Фабрика для AuthenticateServerInteractorImpl
            @Suppress("UNCHECKED_CAST")
            return AuthenticateServerInteractorImpl() as T // <---- Создаем AuthenticateServerInteractorImpl без зависимостей
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
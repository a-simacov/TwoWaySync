package com.synngate.twowaysync.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.data.repository.ServerRepository

class ServerEditViewModelFactory(
    private val serverRepository: ServerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServerEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ServerEditViewModel(serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
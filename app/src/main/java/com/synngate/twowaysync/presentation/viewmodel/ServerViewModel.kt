package com.synngate.twowaysync.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.data.repository.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServerViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _serverStatus = MutableStateFlow<Boolean?>(null)
    val serverStatus: StateFlow<Boolean?> get() = _serverStatus

    fun checkServerConnection() {
        viewModelScope.launch {
            _serverStatus.value = serverRepository.checkServerStatus()
        }
    }
}

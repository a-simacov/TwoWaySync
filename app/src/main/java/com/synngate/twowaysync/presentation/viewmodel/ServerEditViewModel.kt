package com.synngate.twowaysync.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.data.local.db.entities.ExternalServerEntity
import com.synngate.twowaysync.data.repository.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ServerEditViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ServerEditUiState>(ServerEditUiState.Initial)
    val uiState: StateFlow<ServerEditUiState> = _uiState

    private var isModified: Boolean = false
    private var serverId: Int? = null

    fun loadServer(id: Int) {
        viewModelScope.launch {
            val server = serverRepository.getServerById(id).firstOrNull()
            server?.let {
                serverId = it.id!!
                _uiState.value = ServerEditUiState.Loaded(
                    server = it,
                    isActive = serverRepository.isActiveServer(it.id)
                )
            }
        }
    }

    fun updateServerName(name: String) {
        isModified = true
        val currentState = _uiState.value
        if (currentState is ServerEditUiState.Loaded) {
            _uiState.value = currentState.copy(
                server = currentState.server.copy(name = name)
            )
        }
    }

    fun updateServerHost(host: String) {
        isModified = true
        val currentState = _uiState.value
        if (currentState is ServerEditUiState.Loaded) {
            _uiState.value = currentState.copy(
                server = currentState.server.copy(host = host)
            )
        }
    }

    fun updateServerPort(port: Int) {
        isModified = true
        val currentState = _uiState.value
        if (currentState is ServerEditUiState.Loaded) {
            _uiState.value = currentState.copy(
                server = currentState.server.copy(port = port)
            )
        }
    }

    fun saveServer() {
        val currentState = _uiState.value
        if (currentState is ServerEditUiState.Loaded) {
            val server = currentState.server.copy(id = serverId)
            viewModelScope.launch {
                val savedId = serverRepository.saveServer(server)
                if (serverId == null) serverId = savedId
            }
        }
    }

    fun deleteServer() {
        serverId?.let {
            viewModelScope.launch {
                serverRepository.deleteServer(it)
            }
        }
    }

    fun setActiveServer() {
        val currentState = _uiState.value
        if (currentState !is ServerEditUiState.Loaded || isModified) return

        val server = currentState.server

        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = serverRepository.authenticateServer(server)
                if (result.isSuccessful) {
                    serverRepository.setActiveServer(serverId!!)
                    _uiState.value = currentState.copy(isActive = true, isLoading = false)
                } else {
                    _uiState.value = currentState.copy(isLoading = false, errorMessage = "Auth failed")
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(isLoading = false, errorMessage = "Error: ${e.message}")
            }
        }
    }
}

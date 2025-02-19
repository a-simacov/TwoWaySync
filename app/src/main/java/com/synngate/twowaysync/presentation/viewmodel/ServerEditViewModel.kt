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

    private val _serverName = MutableStateFlow("")
    val serverName: StateFlow<String> = _serverName

    private val _serverHost = MutableStateFlow("")
    val serverHost: StateFlow<String> = _serverHost

    private val _serverPort = MutableStateFlow(0)
    val serverPort: StateFlow<Int> = _serverPort

    private var serverId: Int? = null

    fun loadServer(id: Int) {
        viewModelScope.launch {
            val server = serverRepository.getServerById(id).firstOrNull()
            server?.let {
                serverId = it.id
                _serverName.value = it.name
                _serverHost.value = it.host
                _serverPort.value = it.port
            }
        }
    }

    fun updateServerName(name: String) {
        _serverName.value = name
    }

    fun updateServerHost(host: String) {
        _serverHost.value = host
    }

    fun updateServerPort(port: Int) {
        _serverPort.value = port
    }

    fun saveServer() {
        val name = _serverName.value.trim()
        val host = _serverHost.value.trim()
        val port = _serverPort.value
        if (name.isEmpty() || host.isEmpty() || port == 0) return

        viewModelScope.launch {
            val server = ExternalServerEntity(id = serverId!!, name = name, host = host, port = port)
            val savedId = serverRepository.saveServer(server)
            if (serverId == null)
                serverId = savedId//.toInt()
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
        serverId?.let {
            viewModelScope.launch {
                serverRepository.setActiveServer(it)
            }
        }
    }
}

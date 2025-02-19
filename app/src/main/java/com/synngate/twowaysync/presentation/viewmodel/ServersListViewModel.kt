package com.synngate.twowaysync.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.data.local.db.entities.ExternalServerEntity
import com.synngate.twowaysync.data.repository.ServerRepository
import com.synngate.twowaysync.presentation.navigation.NavigationEvent
import com.synngate.twowaysync.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServersListViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    val servers: StateFlow<List<ExternalServerEntity>> =
        serverRepository.getServersFlow()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeServerId: StateFlow<Int?> =
        serverRepository.getActiveServerIdFlow()
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _navigationEvent = MutableStateFlow<NavigationEvent>(NavigationEvent.Empty)
    val navigationEvent: StateFlow<NavigationEvent> = _navigationEvent

    fun navigateToServerEdit(serverId: Int) {
        _navigationEvent.value = NavigationEvent.NavigateTo(Screen.ServerEdit.createRoute(serverId))
    }

    fun navigateToMain() {
        _navigationEvent.value = NavigationEvent.NavigateTo(Screen.Main.route)
    }
}

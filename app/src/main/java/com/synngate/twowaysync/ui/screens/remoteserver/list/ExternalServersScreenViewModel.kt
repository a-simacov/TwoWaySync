package com.synngate.twowaysync.ui.screens.remoteserver.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.di.DataStoreKeys.CURRENT_SERVER_ID_KEY
import com.synngate.twowaysync.domain.interactors.GetExternalServersInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExternalServersScreenViewModel(
    private val getExternalServersInteractor: GetExternalServersInteractor,
    private val navController: NavHostController
) : ViewModel() {

    private val _servers = MutableStateFlow<List<ExternalServer>>(emptyList())
    val servers: StateFlow<List<ExternalServer>> = _servers.asStateFlow()

    private val _serversUi = MutableStateFlow<List<ServerUi>>(listOf(ServerUi.Empty))
    val serversUi: StateFlow<List<ServerUi>> = _serversUi.asStateFlow()

    private val dataStore = MyApplication.appDependencies.dataStore

    val activeServerIdState: StateFlow<Int> = dataStore.data
        .map { preferences ->
            preferences[CURRENT_SERVER_ID_KEY] ?: -1
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = -1
        )

    init {
        loadServers()
        Log.d("slax", "VM init happened")
    }

    private fun loadServers() {
        Log.d("slax", "VM loadServers happened")
        viewModelScope.launch {
            getExternalServersInteractor.execute()
                .collect { serverList ->
                    val uiList = serverList.map { }
                    _servers.value = serverList // uiList
                }
        }
    }

    fun onServerItemClick(serverId: Int) {
        navController.navigate("server_screen/$serverId")
    }
}
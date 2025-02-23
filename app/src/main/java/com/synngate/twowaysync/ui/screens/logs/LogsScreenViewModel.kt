package com.synngate.twowaysync.ui.screens.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.domain.interactors.GetLogsInteractor
import com.synngate.twowaysync.domain.model.LogDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogsScreenViewModel(
    private val getLogsInteractor: GetLogsInteractor,
    private val navController: NavHostController
) : ViewModel() {

    private val _logs = MutableStateFlow<List<LogDetails>>(emptyList())
    val logs: StateFlow<List<LogDetails>> = _logs.asStateFlow()

    init {
        loadServers()
    }

    private fun loadServers() {
        viewModelScope.launch {
            getLogsInteractor.execute(null)
                .collect { logList ->
                    _logs.value = logList // uiList
                }
        }
    }

    fun onServerItemClick(serverId: Int) {
        //navController.navigate("log_screen/$logId")
    }
}
package com.synngate.twowaysync.ui.screens.logs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.interactors.DeleteLogsInteractor
import com.synngate.twowaysync.domain.interactors.GetLogsInteractor
import com.synngate.twowaysync.domain.model.LogDetails
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class LogsScreenViewModel(
    private val getLogsInteractor: GetLogsInteractor,
    private val deleteLogsInteractor: DeleteLogsInteractor,
    private val navController: NavHostController
) : ViewModel() {

    private val _logs = MutableStateFlow<List<LogDetails>>(emptyList())
    val logs: StateFlow<List<LogDetails>> = _logs.asStateFlow()

    private val _filter = MutableStateFlow(LogFilter()) // Текущее состояние фильтра
    val filter: StateFlow<LogFilter> = _filter.asStateFlow()

    private var collect = 0

    init {
        loadLogs()
    }

    private fun loadLogs() {
        collect = 0
        viewModelScope.launch {
            _filter.collect { it ->
                getLogsInteractor.execute(it)
                    .collect { logList ->
                        _logs.value = logList
                        Log.d("slax", "collect $collect")
                        collect++
                        Log.d("slax", it.toString())
                    }
            }
        }
    }

    fun applyFilter(
        event: String,
        level: String,
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?
    ) {
        _filter.value = LogFilter(
            event = event.ifEmpty { null },
            level = level.ifEmpty { null },
            dateFrom,
            dateTo
        )
        TODO("ПРОБЛЕМА ЗДЕСЬ! ДО ЭТОГО ХОТЬ КАК-ТО РАБОТАЛО, НО СБРАСЫВАЛО ФИЛЬТР И ПОКАЗЫВАЛО ВЕСЬ СПИСОК")
        //loadLogs()
        Log.d("slax", "applyFilter")
    }

    fun clearFilter() {
        _filter.value = LogFilter()
    }

    fun addInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            LogHelper.log("INFO")
        }
    }

    fun addError() {
        viewModelScope.launch {
            LogHelper.log("ERROR", "ERROR")
        }
    }

    fun addDebug() {
        viewModelScope.launch {
            LogHelper.log("DEBUG", "DEBUG")
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            try {
                deleteLogsInteractor.execute()
                _logs.value = emptyList()
            } catch (e: Exception) {
                LogHelper.log(e.message ?: "")
            }
        }
    }
}
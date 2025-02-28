package com.synngate.twowaysync.ui.screens.logs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.interactors.DeleteLogsInteractor
import com.synngate.twowaysync.domain.interactors.GetLogsInteractor
import com.synngate.twowaysync.domain.model.LogDetails
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class LogsScreenViewModel(
    private val getLogsInteractor: GetLogsInteractor,
    private val deleteLogsInteractor: DeleteLogsInteractor,
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
        viewModelScope.launch {
            _filter // Используем StateFlow фильтра как источник
                .flatMapLatest { currentFilter ->
                    getLogsInteractor.execute(currentFilter) // Передаем текущий фильтр
                }
                .distinctUntilChanged()
                .collect { logList ->
                    _logs.value = logList
                    Log.d("slax", "collect $collect")
                    collect++
                    Log.d("slax", _filter.value.toString())
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
            dateTimeFrom = dateFrom,
            dateTimeTo = dateTo
        )
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
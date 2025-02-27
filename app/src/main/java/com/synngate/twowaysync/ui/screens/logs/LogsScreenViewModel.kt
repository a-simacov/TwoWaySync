package com.synngate.twowaysync.ui.screens.logs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.synngate.twowaysync.domain.common.LogFilter
import com.synngate.twowaysync.domain.interactors.GetLogsInteractor
import com.synngate.twowaysync.domain.model.LogDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class LogsScreenViewModel(
    private val getLogsInteractor: GetLogsInteractor,
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
            _filter.collect { filter ->
                getLogsInteractor.execute(filter)
                    .collect { logList ->
                        _logs.value = logList
                        Log.d("slax", "collect $collect")
                        collect++
                    }
            }
        }
    }

    fun applyFilter(event: String, level: String, dateFrom: LocalDate?, dateTo: LocalDate?) {
        val fromTimestamp = dateFrom?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val toTimestamp =
            dateTo?.atTime(23, 59, 59)?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

        _filter.value = LogFilter(
            event = event.ifEmpty { null },
            level = level.ifEmpty { null },
            fromTimestamp,
            toTimestamp
        )
        loadLogs()
    }

    fun clearFilter() {
        _filter.value = LogFilter()
    }
}
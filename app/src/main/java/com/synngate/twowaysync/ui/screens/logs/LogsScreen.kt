package com.synngate.twowaysync.ui.screens.logs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.synngate.twowaysync.domain.model.LogDetails
import com.synngate.twowaysync.ui.screens.main.MainScreenButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: LogsScreenViewModel,
    navController: NavController
) {
    val logs by viewModel.logs.collectAsState()
    val filter by viewModel.filter.collectAsState()

    var eventFilter = remember { mutableStateOf(filter.event.orEmpty()) }
    var levelFilter = remember { mutableStateOf(filter.level.orEmpty()) }
    val dateFrom = remember { mutableStateOf<LocalDate?>(null) }
    val dateTo = remember { mutableStateOf<LocalDate?>(null) }
    val isFilterExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Список логов") }) },
        bottomBar = {
            MainScreenButton(
                text = "Закрыть",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                onClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                items(logs) { log ->
                    LogItem(log = log)
                }
            }

            Button(
                onClick = { isFilterExpanded.value = !isFilterExpanded.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(if (isFilterExpanded.value) "Скрыть фильтр" else "Показать фильтр")
            }

            AnimatedVisibility(visible = isFilterExpanded.value) {
                LogsFilterSection(
                    eventFilter = eventFilter,
                    levelFilter = levelFilter,
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    applyFilter = {
                        viewModel.applyFilter(
                            eventFilter.value,
                            levelFilter.value,
                            dateFrom.value,
                            dateTo.value
                        )
                    },
                    clearFilter = {
                        viewModel.clearFilter()
                        eventFilter.value = ""
                        levelFilter.value = ""
                        dateFrom.value = null
                        dateTo.value = null
                        viewModel.applyFilter("", "", null, null)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsFilterSection(
    eventFilter: MutableState<String>,
    levelFilter: MutableState<String>,
    dateFrom: MutableState<LocalDate?>,
    dateTo: MutableState<LocalDate?>,
    applyFilter: () -> Unit,
    clearFilter: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val errorLevels = listOf("INFO", "WARN", "ERROR", "DEBUG")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = eventFilter.value,
            onValueChange = { eventFilter.value = it },
            label = { Text("Событие") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = levelFilter.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Уровень ошибки") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expanded = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                errorLevels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            levelFilter.value = level
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DatePickerField("Дата с", dateFrom)
        Spacer(modifier = Modifier.height(8.dp))
        DatePickerField("Дата по", dateTo)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = clearFilter,
                modifier = Modifier.weight(1f)
            ) {
                Text("Очистить")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = applyFilter,
                modifier = Modifier.weight(1f)
            ) {
                Text("Применить")
            }
        }
    }
}

@Composable
fun DatePickerField(label: String, selectedDate: MutableState<LocalDate?>) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate.value = LocalDate.of(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = selectedDate.value?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Выбрать дату")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun LogItem(log: LogDetails, modifier: Modifier = Modifier) {
    val backgroundColor = when (log.level.lowercase()) {
        "error" -> Color.Red.copy(alpha = 0.1f)
        "warning" -> Color.Yellow.copy(alpha = 0.1f)
        "info" -> Color.Blue.copy(alpha = 0.1f)
        else -> Color.Gray.copy(alpha = 0.05f)
    }

    val levelColor = when (log.level.lowercase()) {
        "error" -> Color.Red
        "warning" -> Color(0xFFFFA500) // Оранжевый
        "info" -> Color.Blue
        else -> Color.Gray
    }

    val formattedDate = remember(log.dateTime) {
        log.dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Light),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.event,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.level.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = levelColor
            )
        }
    }
}

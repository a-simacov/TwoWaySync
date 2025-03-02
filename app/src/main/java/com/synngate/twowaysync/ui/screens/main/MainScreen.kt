package com.synngate.twowaysync.ui.screens.main

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val MainScreenButtonVerticalSpacing = 16.dp
private val MainScreenStatusBottomPadding = 16.dp
private val MainScreenPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    onCloseClicked: () -> Unit,
    onLogsClicked: () -> Unit,
    onProductsClicked: () -> Unit
) {
    val productsCount by viewModel.productsCount.collectAsStateWithLifecycle()
    val logsCount by viewModel.logsCount.collectAsStateWithLifecycle()
    val isActualServerServiceRunning by viewModel.iaActualServerCheckIsRunningStateFlow.collectAsStateWithLifecycle()
    val lastServerStatus by viewModel.actualServerStatusFlow.collectAsStateWithLifecycle()
    val lastServerCheckTime by viewModel.actualServerCheckTimeFlow.collectAsStateWithLifecycle()
    val isWebServerServiceRunning by viewModel.isWebServerCheckIsRunningStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Главный экран") }) }, bottomBar = {
            MainScreenButton(
                text = "Закрыть",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = MainScreenPadding,
                        start = MainScreenPadding,
                        end = MainScreenPadding
                    ),
                onClick = onCloseClicked
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(MainScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MainScreenButtonVerticalSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                MainScreenButton(
                    text = "Логи: $logsCount",
                    onClick = onLogsClicked
                )
                MainScreenButton(
                    text = "Товары: $productsCount",
                    onClick = onProductsClicked
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 8.dp)
                ServiceStatusSection(viewModel, context, isActualServerServiceRunning)
                LocalServerStatusSection(viewModel, context, isWebServerServiceRunning)
            }

            ServerStatusRow(
                lastServerStatus = lastServerStatus,
                lastServerCheckTime = lastServerCheckTime,
                //localWebServerStatus = mainScreenData.localWebServerStatus
            )
        }
    }
}

@Composable
private fun ServiceStatusSection(
    viewModel: MainScreenViewModel,
    context: Context,
    isServiceRunning: Boolean
) {
    CheckActiveServiceCommands(viewModel, context, isServiceRunning)
}

@Composable
private fun LocalServerStatusSection(
    viewModel: MainScreenViewModel,
    context: Context,
    isWebServerServiceRunning: Boolean
) {
    LocalServerServiceCommands(viewModel, context, isWebServerServiceRunning)
}

@Composable
private fun ServerStatusRow(
    lastServerStatus: String,
    lastServerCheckTime: String,
    //localWebServerStatus: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = MainScreenStatusBottomPadding)
    ) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(
                text = "Удаленный сервер:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Запущен: $lastServerStatus -> $lastServerCheckTime")
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(
                text = "Локальный веб-сервер:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "TODO")
        }
    }
}

@Composable
private fun CheckActiveServiceCommands(
    viewModel: MainScreenViewModel,
    context: Context,
    serviceStatus: Boolean
) {
    Row {
        MainScreenButton(
            onClick = { viewModel.startCheckServerService(context) },
            enabled = !serviceStatus,
            text = "Запустить сервис проверки",
            modifier = Modifier.weight(1f)
        )
        MainScreenButton(
            onClick = { viewModel.stopCheckServerService(context) },
            enabled = serviceStatus,
            text = "Остановить сервис проверки",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LocalServerServiceCommands(
    viewModel: MainScreenViewModel,
    context: Context,
    serviceStatus: Boolean
) {
    Row {
        MainScreenButton(
            onClick = { viewModel.startWebServerService(context) },
            enabled = !serviceStatus,
            text = "Запустить веб-сервер",
            modifier = Modifier.weight(1f)
        )
        MainScreenButton(
            onClick = { viewModel.stopWebServerService(context) },
            enabled = serviceStatus,
            text = "Остановить веб-сервер",
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun MainScreenButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RectangleShape,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

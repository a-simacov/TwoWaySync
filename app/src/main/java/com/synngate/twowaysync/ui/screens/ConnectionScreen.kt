package com.synngate.twowaysync.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.synngate.twowaysync.R
import com.synngate.twowaysync.ui.screens.viewmodel.ConnectionScreenViewModel
import com.synngate.twowaysync.ui.screens.viewmodel.ConnectionScreenViewModelFactory
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(navController: NavHostController, serverId: Int?) { // <---- Принимаем navController и serverId
    val context = LocalContext.current
    val factory = ConnectionScreenViewModelFactory(context = context)
    val viewModel: ConnectionScreenViewModel = viewModel(factory = factory)

    // Загрузка деталей сервера при первом создании Composable
    LaunchedEffect(serverId) { // <---- LaunchedEffect для выполнения кода при изменении serverId
        if (serverId != null) {
            viewModel.loadServerDetails(serverId) // <---- Загружаем детали сервера в ViewModel
        }
    }

    ConnectionScreenContent(viewModel = viewModel) // <---- Передаем ViewModel в Content функцию
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreenContent(viewModel: ConnectionScreenViewModel) {
    val server by viewModel.server.collectAsState() // <---- State для деталей сервера
    val connectionStatus by viewModel.connectionStatus.collectAsState() // <---- State для статуса подключения

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.connection_screen_title)) }) // <---- TopAppBar с названием экрана
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // <---- Расстояние между элементами Column
        ) {
            // Информация о сервере
            Text(
                text = stringResource(id = R.string.connection_server_name_label) + ": ${server?.name ?: "Неизвестно"}", // <---- Имя сервера
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(id = R.string.connection_server_host_label) + ": ${server?.host ?: "Неизвестно"}", // <---- Хост сервера
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(id = R.string.connection_server_port_label) + ": ${server?.port ?: "Неизвестно"}", // <---- Порт сервера
                style = MaterialTheme.typography.bodyLarge
            )

            // Статус подключения
            Text(
                text = stringResource(id = R.string.connection_status_label) + ": $connectionStatus", // <---- Статус подключения
                style = MaterialTheme.typography.bodyLarge
            )

            // Кнопка "Подключиться"
            Button(onClick = { viewModel.connectToServer() }) { // <---- Кнопка "Подключиться"
                Text(stringResource(id = R.string.connection_button_label)) // <---- Текст кнопки
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ConnectionScreenPreview() {
    TwoWaySyncTheme {
        ConnectionScreenContent(viewModel = viewModel()) //  viewModel() без фабрики для Preview
        // ConnectionScreen(navController = rememberNavController(), serverId = 1) //  Использовать для тестирования с NavController и serverId
    }
}
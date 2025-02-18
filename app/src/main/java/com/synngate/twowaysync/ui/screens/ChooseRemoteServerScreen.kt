package com.synngate.twowaysync.ui.screens //  <----  Укажите ваш пакет для Compose UI экранов

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // <----  Импорт material3 вместо material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import com.synngate.twowaysync.ui.screens.viewmodel.ChooseRemoteServerScreenViewModel
import com.synngate.twowaysync.ui.screens.viewmodel.ChooseRemoteServerScreenViewModelFactory
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme

@Composable
fun ChooseRemoteServerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val factory = ChooseRemoteServerScreenViewModelFactory(context = context, navController = navController)
    val viewModel: ChooseRemoteServerScreenViewModel = viewModel(factory = factory)

    ChooseRemoteServerScreenContent(viewModel = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseRemoteServerScreenContent(viewModel: ChooseRemoteServerScreenViewModel, navController: NavHostController) {
    val servers by viewModel.servers.collectAsState() // <---- Собираем StateFlow из ViewModel

    val savedServerIdState = viewModel.savedServerIdState.collectAsStateWithLifecycle() // <---- Получаем savedServerIdState из ViewModel
    val savedServerId = savedServerIdState.value // <---- Получаем значение State<Int?> в виде Int?

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Choose remote server to connect") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("remote_server_screen") // <---- Навигация на RemoteServerScreen
            }) {
                Icon(Icons.Filled.Add, "Add server")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (servers.isEmpty()) { // <---- Проверяем, есть ли серверы в списке
                Text(
                    text = "Список серверов пуст. Добавьте сервер.", // <---- Сообщение, если список пуст
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn { // <---- Используем LazyColumn для отображения списка серверов
                    items(servers) { server ->
                        ServerItem(
                            server = server,
                            savedServerId = savedServerId,
                            onServerItemClick = { serverId -> viewModel.onServerItemClick(serverId) }, // <---- Обработчик клика
                            onServerItemLongClick = { serverId -> viewModel.onServerItemLongClick(serverId) } // <---- Обработчик долгого клика
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ServerItem(
    server: RemoteServerDetails,
    savedServerId: Int?,
    onServerItemClick: (Int) -> Unit,
    onServerItemLongClick: (Int) -> Unit
) {
    val isSelectedServer = server.id == savedServerId // <----  1. ОПРЕДЕЛЕНИЕ: Сравниваем server.serverId с savedServerId
    val backgroundColor = if (isSelectedServer) { // <----  2. ВЫБОР ЦВЕТА: Проверяем isSelectedServer
        MaterialTheme.colorScheme.secondaryContainer // <----  3a. ЦВЕТ ДЛЯ ВЫДЕЛЕННОГО: Если isSelectedServer == true, используем secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow // <----  3b. ЦВЕТ ДЛЯ НЕ ВЫДЕЛЕННОГО: Иначе (isSelectedServer == false), используем surfaceContainerLow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(  // <---- Заменили .clickable на .combinedClickable
                onClick = { server.id?.let { onServerItemClick(it) } }, // <---- Обработка обычного клика остается
                onLongClick = { server.id?.let { onServerItemLongClick(it) } } // <---- Добавили обработку долгого клика
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = server.name, // <---- Отображаем serverName
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "${server.host}:${server.port}", // <---- Отображаем host и port
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChooseRemoteServerScreenPreview() {
    TwoWaySyncTheme {
        val navController = rememberNavController()
        ChooseRemoteServerScreenContent(
            viewModel = viewModel(), //  viewModel() без фабрики для Preview
            navController = navController
        )
    }
}
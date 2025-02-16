package com.synngate.twowaysync.ui.screens //  <----  Укажите ваш пакет для Compose UI экранов

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // <----  Импорт material3 вместо material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseRemoteServerScreen(
    onAddButtonClick: () -> Unit, // <----  Параметр обработчика кнопки "Add"
    onServerItemClick: (serverId: Int) -> Unit, // <----  Добавляем параметр для обработки быстрого нажатия на элемент списка
    onServerItemLongClick: (serverId: Int) -> Unit // <----  Добавляем параметр для обработки долгого нажатия на элемент списка
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Choose remote server to connect") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddButtonClick) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            val servers = listOf(
                RemoteServerItem("Server 1", "host1", 1234, 1),
                RemoteServerItem("Server 2", "host2", 5678, 2),
                RemoteServerItem("Server 3", "host3", 9012, 3)
            )

            if (servers.isEmpty()) {
                item {
                    Text("Remote servers list is empty")
                }
            } else {
                items(servers) { server ->
                    ServerItem(
                        server = server,
                        onClick = { onServerItemClick(server.id) }, // <----  Вызываем onServerItemClick с ID сервера при быстром нажатии
                        onLongClick = { onServerItemLongClick(server.id) } // <----  Вызываем onServerItemLongClick с ID сервера при долгом нажатии
                    )
                }
            }
        }
    }
}

data class RemoteServerItem(
    val name: String,
    val host: String,
    val port: Int,
    val id: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ServerItem(
    server: RemoteServerItem,
    onClick: () -> Unit, // <----  Добавляем параметр onClick: лямбда-функция без параметров и возвращаемого значения
    onLongClick: () -> Unit // <----  Добавляем параметр onLongClick: лямбда-функция без параметров и возвращаемого значения
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick, // <----  Передаем лямбду onClick в combinedClickable
                onLongClick = onLongClick // <----  Передаем лямбду onLongClick в combinedClickable
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = server.name,
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Хост: ${server.host}:${server.port}",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "ID: ${server.id}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseRemoteServerScreenPreview() {
    val navController = rememberNavController() // <----  Создаем NavController для Preview
    TwoWaySyncTheme {
        ChooseRemoteServerScreen(
            onAddButtonClick = { // <----  Реализация onAddButtonClick для Preview - навигация к RemoteServerScreen
                navController.navigate("remote_server_screen") //  Переход к RemoteServerScreen в Preview
            },
            onServerItemClick = { serverId -> // <----  Реализация onServerItemClick для Preview - навигация к RemoteServerConnectionScreen
                println("Preview: Быстрое нажатие на сервер ID: $serverId") // Для Preview логируем действие
                navController.navigate("remote_server_connection_screen") // Переход к RemoteServerConnectionScreen в Preview
            },
            onServerItemLongClick = { serverId -> // <----  Реализация onServerItemLongClick для Preview - навигация к RemoteServerCommandsScreen
                println("Preview: Долгое нажатие на сервер ID: $serverId") // Для Preview логируем действие
                navController.navigate("remote_server_commands_screen") // Переход к RemoteServerCommandsScreen в Preview
            }
        )
    }
}
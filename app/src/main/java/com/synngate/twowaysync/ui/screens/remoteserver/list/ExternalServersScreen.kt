package com.synngate.twowaysync.ui.screens.remoteserver.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.synngate.twowaysync.domain.model.ExternalServer
import com.synngate.twowaysync.ui.screens.main.MainScreenButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalServersScreen(
    factory: ExternalServersScreenViewModelFactory,
    navController: NavHostController
) {
    val viewModel: ExternalServersScreenViewModel = viewModel(factory = factory)

    val servers by viewModel.servers.collectAsState()

    val activeServerIdState = viewModel.activeServerIdState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Список внешних серверов") }) },
        floatingActionButton = { NewExternalServerButton(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (servers.isEmpty()) {
                Text(
                    text = "Список пуст.\nДобавьте сервер для продолжения работы.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(servers) { server ->
                        ExternalServerItem(
                            server = server,
                            savedServerId = activeServerIdState.value,
                            onServerItemClick = { serverId -> viewModel.onServerItemClick(serverId) },
                        )
                    }
                }
            }
            MainScreenButton(
                text = "Продолжить",
                onClick = { navController.navigate("main_screen") },
                enabled = activeServerIdState.value >= 0
            )
        }
    }
}

@Composable
fun NewExternalServerButton(navController: NavHostController, modifier: Modifier = Modifier) {
    FloatingActionButton(
        modifier = Modifier.padding(bottom = 80.dp),
        onClick = { navController.navigate("server_screen/-1")}
    ) {
        Icon(Icons.Filled.Add, "Add server")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExternalServerItem(
    server: ExternalServer,
    savedServerId: Int?,
    onServerItemClick: (Int) -> Unit
) {
    val isSelectedServer = server.id == savedServerId
    val backgroundColor = if (isSelectedServer) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { server.id?.let { onServerItemClick(it) } },
                onLongClick = { server.id?.let { onServerItemClick(it) } }
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${server.name} (id: ${server.id})",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "${server.host}:${server.port}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

interface ServerUi {

    @Composable
    fun Show(modifier: Modifier, onClick: () -> Unit)

    class IsActive : ServerUi {

        @Composable
        override fun Show(modifier: Modifier, onClick: () -> Unit) {

        }
    }

    class IsNotActive : ServerUi {

        @Composable
        override fun Show(modifier: Modifier, onClick: () -> Unit) {

        }
    }

    object Empty : ServerUi {

        @Composable
        override fun Show(modifier: Modifier, onClick: () -> Unit) {
            Text(
                text = "Список пуст.\nДобавьте сервер для продолжения работы.",
                modifier = modifier
                    .fillMaxWidth()
                    //.weight(1f)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
package com.synngate.twowaysync.ui.screens.remoteserver.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.synngate.twowaysync.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalServerScreen(
    factory: ExternalServerScreenViewModelFactory,
    navController: NavHostController,
    serverId: Int
) {
    val viewModel: ExternalServerScreenViewModel = viewModel(factory = factory)

    val state by viewModel.uiState.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    LaunchedEffect(serverId) {
        viewModel.load(serverId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Внешний сервер (${state.id})") })
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                onClick = { navController.popBackStack() }
            ) {
                Text("Закрыть")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = state.serverName,
                onValueChange = { newValue -> viewModel.updateName(newValue) },
                label = { Text("Имя сервера *") },
                placeholder = { Text("Введите имя сервера") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = state.serverNameError.isNotEmpty(),
                supportingText = {
                    if (state.serverNameError.isNotEmpty()) {
                        Text(text = state.serverNameError)
                    }
                }
            )

            TextField(
                value = state.host,
                onValueChange = { newValue -> viewModel.updateHost(newValue) },
                label = { Text("Хост *") },
                placeholder = { Text("Введите хост") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = state.hostError.isNotEmpty(),
                supportingText = {
                    if (state.hostError.isNotEmpty()) {
                        Text(text = state.hostError)
                    }
                }
            )

            TextField(
                value = state.port,
                onValueChange = { newValue -> viewModel.updatePort(newValue) },
                label = { Text("Порт *") },
                placeholder = { Text("Введите порт") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = state.portError.isNotEmpty(),
                supportingText = {
                    if (state.portError.isNotEmpty()) {
                        Text(text = state.portError)
                    }
                }
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.save() }
            ) {
                Text("Сохранить")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.delete() }
            ) {
                Text("Удалить")
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 16.dp
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.testConnection() }
            ) {
                Text("Проверить подключение")
            }
            Text(
                text = stringResource(id = R.string.connection_status_label) + ": $connectionStatus",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.toggleActiveStatus() }
            ) {
                Text(text = state.isActiveText)
            }
        }
    }
}

interface ActiveServerButton {

    @Composable
    fun Show(modifier: Modifier, onClick: () -> Unit)

    class IsActive : ActiveServerButton {
        @Composable
        override fun Show(modifier: Modifier, onClick: () -> Unit) {

        }
    }

    class IsNotActive : ActiveServerButton {
        @Composable
        override fun Show(modifier: Modifier, onClick: () -> Unit) {

        }
    }
}
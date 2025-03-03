package com.synngate.twowaysync.ui.screens.remoteserver.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.synngate.twowaysync.R
import com.synngate.twowaysync.ui.screens.main.MainScreenButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalServerScreen(
    viewModel: ExternalServerScreenViewModel,
    serverId: Int,
    onClickDelete: () -> Unit,
    onClickClose: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    val showDeleteConfirmationDialog by viewModel.showDeleteConfirmationDialog.collectAsStateWithLifecycle()
    val isServerDeleted by viewModel.isServerDeleted.collectAsStateWithLifecycle()

    if (showDeleteConfirmationDialog) {
        DeleteConfirmationDialog(
            onDismiss = viewModel::onDismissDeleteConfirmationDialog,
            onConfirm = viewModel::onDeleteConfirmed
        )
    }

    LaunchedEffect(key1 = isServerDeleted) {
        if (isServerDeleted) {
            onClickDelete()
        }
    }

    LaunchedEffect(serverId) {
        viewModel.load(serverId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Внешний сервер (${state.id})") })
        },
        bottomBar = {
            MainScreenButton(
                text = "Закрыть",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                onClick = { onClickClose.invoke() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
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

            MainScreenButton(
                text = "Сохранить",
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.save() }
            )
            MainScreenButton(
                text = "Удалить",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.onShowDeleteConfirmationDialog()
                }
            )
            HorizontalDivider(
                thickness = 4.dp
            )
            MainScreenButton(
                text = "Проверить подключение",
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.testConnection() }
            )
            Text(
                text = stringResource(id = R.string.connection_status_label) + ": $connectionStatus",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            MainScreenButton(
                text = state.isActiveText,
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.toggleActiveStatus() }
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Подтверждение удаления") },
        text = { Text("Вы уверены, что хотите удалить сервер?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Удалить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
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
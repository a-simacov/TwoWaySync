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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val showDeleteConfirmationDialog by viewModel.showDeleteConfirmationDialog.collectAsStateWithLifecycle()
    val isServerDeleted by viewModel.isServerDeleted.collectAsStateWithLifecycle()

    val uiComponents = listOf(
        ExternalServerFields(viewModel = viewModel),
        ExternalServerActions(viewModel = viewModel)
    )

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            uiComponents.forEach { uiComponent ->
                uiComponent.Render(
                    state = state,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        DeleteConfirmationDialog(
            onDismiss = viewModel::onDismissDeleteConfirmationDialog,
            onConfirm = viewModel::onDeleteConfirmed
        )
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is ExternalServerEvent.DeleteSuccess)
                event.showSnackbar(
                    context = context,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    actionLabel = "Отменить удаление",
                    onActionPerformed = { viewModel.undoDelete() },
                    onActionDismissed = { viewModel.serverDeleted() }
                )
            else
                event.show(context)
        }
    }

    LaunchedEffect(key1 = isServerDeleted) {
        if (isServerDeleted) {
            onClickDelete()
        }
    }

    LaunchedEffect(serverId) {
        viewModel.load(serverId)
    }
}

private abstract class UiComponent(protected val viewModel: ExternalServerScreenViewModel) {

    @Composable
    abstract fun Render(state: ExternalServerUiState, modifier: Modifier)
}

private class ExternalServerFields(viewModel: ExternalServerScreenViewModel) :
    UiComponent(viewModel) {

    @Composable
    override fun Render(state: ExternalServerUiState, modifier: Modifier) {
        ServerTextField(
            value = state.serverName,
            error = state.serverNameError,
            label = "Имя сервера *",
            placeHolder = "Введите имя сервера",
            onChange = { newValue -> viewModel.updateName(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        ServerTextField(
            value = state.host,
            error = state.hostError,
            label = "Хост *",
            placeHolder = "Введите хост",
            onChange = { newValue -> viewModel.updateHost(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        ServerTextField(
            value = state.port,
            error = state.portError,
            label = "Порт *",
            placeHolder = "Введите порт",
            onChange = { newValue -> viewModel.updatePort(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
private fun ServerTextField(
    value: String,
    error: String,
    label: String,
    placeHolder: String,
    onChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions
) {
    TextField(
        value = value,
        onValueChange = { newValue -> onChange(newValue) },
        label = { Text(label) },
        placeholder = { Text(placeHolder) },
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        isError = error.isNotEmpty(),
        supportingText = {
            if (error.isNotEmpty()) {
                Text(text = error)
            }
        }
    )
}

private class ExternalServerActions(viewModel: ExternalServerScreenViewModel) :
    UiComponent(viewModel) {

    @Composable
    override fun Render(state: ExternalServerUiState, modifier: Modifier) {
        val connectionStatus by viewModel.connectionStatus.collectAsStateWithLifecycle()

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
            modifier = modifier//.align(alignment = Alignment.CenterHorizontally)
        )
        MainScreenButton(
            text = state.isActiveText,
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.toggleActiveStatus() }
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
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
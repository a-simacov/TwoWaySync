package com.synngate.twowaysync.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synngate.twowaysync.MyApp
import com.synngate.twowaysync.presentation.viewmodel.ServerEditUiState
import com.synngate.twowaysync.presentation.viewmodel.ServerEditViewModel
import com.synngate.twowaysync.presentation.viewmodel.ServerEditViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerEditScreen(
    serverId: Int?,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
) {
    val serverRepository = (LocalContext.current.applicationContext as MyApp).serverRepository
    val viewModel: ServerEditViewModel = viewModel(factory = ServerEditViewModelFactory(serverRepository))

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serverId) {
        serverId?.let { viewModel.loadServer(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Server") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                when (uiState) {
                    is ServerEditUiState.Loading -> {
                        Text("Loading...")
                    }
                    else -> {
                        val state = uiState as ServerEditUiState.Loaded
                        OutlinedTextField(
                            value = state.server.name,
                            onValueChange = viewModel::updateServerName,
                            label = { Text("Server Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.server.host,
                            onValueChange = viewModel::updateServerHost,
                            label = { Text("Server Host") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.server.port.toString(),
                            onValueChange = { viewModel.updateServerPort(it.toIntOrNull() ?: 0) },
                            label = { Text("Server Port") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = onBack) {
                                Text("Cancel")
                            }
                            Button(onClick = {
                                viewModel.saveServer()
                                onSave()
                            }) {
                                Text("Save")
                            }
                            Button(
                                onClick = { viewModel.setActiveServer() },
                                colors = ButtonDefaults.buttonColors(containerColor = if (state.isActive) Color.Gray else Color.Green),
                                enabled = !state.isActive && !state.isLoading
                            ) {
                                Text("Set Active")
                            }
                        }
                        if (state.errorMessage != null) {
                            Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
                        }
                        if (state.isLoading) {
                            CircularProgressIndicator()
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (serverId != null) {
                            Button(
                                onClick = {
                                    viewModel.deleteServer()
                                    onDelete()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Delete", color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                }
            }
        }
    )
}

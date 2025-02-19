package com.synngate.twowaysync.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synngate.twowaysync.presentation.viewmodel.ServerEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerEditScreen(
    serverId: Int?,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    onSetActive: () -> Unit,
    viewModel: ServerEditViewModel = viewModel()
) {
    val serverName by viewModel.serverName.collectAsState()
    val serverHost by viewModel.serverHost.collectAsState()
    val serverPort by viewModel.serverPort.collectAsState()

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
                OutlinedTextField(
                    value = serverName,
                    onValueChange = viewModel::updateServerName,
                    label = { Text("Server Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = serverHost,
                    onValueChange = viewModel::updateServerHost,
                    label = { Text("Server Host") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = serverPort.toString(),
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
                    Button(onClick = onSetActive, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                        Text("Set Active")
                    }
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
    )
}

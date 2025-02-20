package com.synngate.twowaysync.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synngate.twowaysync.MyApp
import com.synngate.twowaysync.data.local.db.entities.ExternalServerEntity
import com.synngate.twowaysync.presentation.navigation.NavigationEvent
import com.synngate.twowaysync.presentation.viewmodel.ServersListViewModel
import com.synngate.twowaysync.presentation.viewmodel.ServersListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersListScreen(
    onNavigate: (NavigationEvent) -> Unit,
    onServerClick: (Int) -> Unit,
    onAddServerClick: () -> Unit,
    onContinueClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val serverRepository = (LocalContext.current.applicationContext as MyApp).serverRepository
    val viewModel: ServersListViewModel = viewModel(factory = ServersListViewModelFactory(serverRepository))

    val servers by viewModel.servers.collectAsState()
    val activeServerId by viewModel.activeServerId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Servers") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddServerClick) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(servers) { server ->
                    ServerItem(
                        server = server,
                        isActive = server.id == activeServerId,
                        onClick = { onServerClick(server.id!!) }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onExitClick) {
                    Text("Exit")
                }
                Button(onClick = onContinueClick, enabled = activeServerId != null) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun ServerItem(server: ExternalServerEntity, isActive: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = if (isActive) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) else CardDefaults.cardColors()
    ) {
        Text(
            text = server.name,
            modifier = Modifier.padding(16.dp)
        )
    }
}

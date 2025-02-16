package com.synngate.twowaysync.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme

@Composable
fun CommandsScreen(serverId: Int?) { // <---- Принимаем serverId как аргумент
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Экран управления сервером (команды). ServerId: ${serverId ?: "не передан"}") // <---- Отображаем ServerId
    }
}

@Preview(showBackground = true)
@Composable
fun CommandsScreenPreview() {
    TwoWaySyncTheme {
        CommandsScreen(serverId = 1) // <---- Пример serverId для Preview
    }
}
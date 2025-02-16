package com.synngate.twowaysync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.synngate.twowaysync.data.interactors.fake.FakeSaveRemoteServerSettingsInteractor
import com.synngate.twowaysync.ui.screens.viewmodel.RemoteServerScreenViewModel
import com.synngate.twowaysync.ui.screens.viewmodel.RemoteServerScreenViewModelFactory
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteServerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val factory = RemoteServerScreenViewModelFactory(context = context, navController = navController) // <----  Создаем фабрику, передавая Context и NavController
    val viewModel: RemoteServerScreenViewModel = viewModel(factory = factory) // <----  Получаем ViewModel через фабрику

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Connection") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Настройки подключения",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = state.serverName,
                onValueChange = { newValue -> viewModel.updateServerName(newValue) },
                label = { Text("Имя сервера *") },
                placeholder = { Text("Введите имя сервера") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                isError = state.serverNameError.isNotEmpty(), // <----  isError проверяет на .isNotEmpty() вместо != null
                supportingText = {
                    if (state.serverNameError.isNotEmpty()) { // <----  Проверка на .isNotEmpty() вместо != null
                        Text(text = state.serverNameError)
                    }
                }
            )

            TextField(
                value = state.host,
                onValueChange = { newValue -> viewModel.updateHost(newValue) },
                label = { Text("Хост *") },
                placeholder = { Text("Введите хост") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                isError = state.hostError.isNotEmpty(), // <----  isError проверяет на .isNotEmpty() вместо != null
                supportingText = {
                    if (state.hostError.isNotEmpty()) { // <----  Проверка на .isNotEmpty() вместо != null
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                isError = state.portError.isNotEmpty(), // <----  isError проверяет на .isNotEmpty() вместо != null
                supportingText = {
                    if (state.portError.isNotEmpty()) { // <----  Проверка на .isNotEmpty() вместо != null
                        Text(text = state.portError)
                    }
                }
            )

            TextField(
                value = state.authEndpoint,
                onValueChange = { newValue -> viewModel.updateAuthEndpoint(newValue) },
                label = { Text("Auth endpoint") },
                placeholder = { Text("Введите Auth endpoint (по умолчанию /auth)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = state.echoEndpoint,
                onValueChange = { newValue -> viewModel.updateEchoEndpoint(newValue) },
                label = { Text("Echo endpoint") },
                placeholder = { Text("Введите Echo endpoint (по умолчанию /echo)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = state.username,
                onValueChange = { newValue -> viewModel.updateUsername(newValue) },
                label = { Text("Имя пользователя") },
                placeholder = { Text("Введите имя пользователя (необязательно)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = state.password,
                onValueChange = { newValue -> viewModel.updatePassword(newValue) },
                label = { Text("Пароль") },
                placeholder = { Text("Введите пароль (необязательно, минимум 6 символов, если указан пользователь)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = state.passwordError.isNotEmpty(), // <----  isError проверяет на .isNotEmpty() вместо != null
                supportingText = {
                    if (state.passwordError.isNotEmpty()) { // <----  Проверка на .isNotEmpty() вместо != null
                        Text(text = state.passwordError)
                    }
                }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    viewModel.saveServerSettings()
                }) {
                    Text("Сохранить")
                }
                Button(onClick = {
                    viewModel.testConnection()
                }) {
                    Text("Test connection")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RemoteServerScreenPreview() {
    TwoWaySyncTheme {
        val navController = rememberNavController() //  Создаем NavController для Preview
        RemoteServerScreen(navController = navController)
    }
}
package com.synngate.twowaysync.data.source.remote

import android.content.Context
import android.os.Build
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.util.NetworkUtils
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.application.host
import io.ktor.server.application.install
import io.ktor.server.application.port
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.contentLength
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import java.net.ServerSocket

class KtorDeviceServer(private val context: Context) {

    companion object {
        private const val DEFAULT_PORT = 6868
        private const val SHUTDOWN_GRACE_PERIOD_MS = 500L
        private const val SHUTDOWN_TIMEOUT_MS = 1000L
        private const val PRODUCTS_ENDPOINT = "/products"
        private const val PING_ENDPOINT = "/ping"
        private const val STATUS_ENDPOINT = "/status"
        private const val ROOT_ENDPOINT = "/"
    }

    private var isRunning = false
    private var lastError: Throwable? = null
    private var isStarted = false

    private val _serverState = MutableStateFlow("")
    val serverState: StateFlow<String> = _serverState
        .asStateFlow()
        .stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val server: ApplicationEngine by lazy {
        embeddedServer(Netty, DEFAULT_PORT) { // Если указать порт = 0, то он будет выбран автоматом
            environment.monitor.subscribe(ApplicationStarted) {
                isStarted = true
                _serverState.value = "Сервер запущен $host"
            }
            environment.monitor.subscribe(ApplicationStopped) {
                isStarted = false
                _serverState.value = "Сервер остановлен"
            }
            install(CallLogging) {
                level = Level.INFO
                filter { call -> call.request.path().startsWith("/") }
            }
            configureServer()
            configureRouting()
        }
    }

    val host: String
        get() = String.format("%s:%d", NetworkUtils.getLocalIpAddress(), DEFAULT_PORT)

    fun startServer() {
        if (!isRunning && isPortAvailable(DEFAULT_PORT)) {
            try {
                lastError = null
                server.start(wait = false)
                isRunning = true
            } catch (e: Throwable) {
                lastError = e
                isRunning = false
                _serverState.value = "Ошибка запуска сервера: ${e.message}"
            }
        } else
            _serverState.value = "Порт $DEFAULT_PORT занят"
    }

    fun stopServer() {
        if (isRunning) {
            server.stop(SHUTDOWN_GRACE_PERIOD_MS, SHUTDOWN_TIMEOUT_MS)
            isRunning = false
        }
    }

    private fun isPortAvailable(port: Int): Boolean {
        return try {
            ServerSocket(port).use { true }
        } catch (e: Exception) {
            false
        }
    }

    fun isRunning() = isRunning && isStarted && lastError == null

    fun getLastError(): Throwable? {
        return lastError
    }

    fun getHostname(): String = server.environment.config.host

    fun getListeningPort(): Int = server.environment.config.port

    private fun Application.configureServer() {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
        install(StatusPages) {
            exception<ContentTransformationException> { call, cause ->
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Invalid request body format. Expected a JSON array of ProductDetails objects.")
                )
            }
            exception<SerializationException> { call, cause ->
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Invalid request body format. ${cause.message}")
                )
            }
            exception<RequestValidationException> { call, cause ->
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Invalid request body format. ${cause.reasons.joinToString()}")
                )
            }
            exception<Exception> { call, cause ->
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Internal server error occurred. ${cause.message}")
                )
                println("Error: ${cause.message}")
            }
        }
    }

    private fun Application.configureRouting() {
        routing {
            intercept(ApplicationCallPipeline.Plugins) {
                val method = call.request.httpMethod.value
                val uri = call.request.uri
                val contentLength = call.request.contentLength() ?: 0
                _serverState.value = "Request: $method $uri Content-Length: $contentLength"
                proceed()
            }
            post(PRODUCTS_ENDPOINT) {
                val receivedProducts = call.receive<List<ProductDetails>>()
                saveToDatabase(receivedProducts)
                call.respond(HttpStatusCode.OK, "Data received and saved")
            }
            get(PING_ENDPOINT) {
                call.respond(HttpStatusCode.OK, "Pong")
            }
            get(STATUS_ENDPOINT) {
                call.respond(mapOf("status" to "running"))
            }
            get(ROOT_ENDPOINT) {
                call.respond("Hello!! You are here in ${Build.MODEL}")
            }
        }
    }

    private suspend fun saveToDatabase(products: List<ProductDetails>) {
        val appDependencies = (context.applicationContext as MyApplication).appDependencies
        val repository = appDependencies.productRepository
        repository.insertProducts(products)
    }
}
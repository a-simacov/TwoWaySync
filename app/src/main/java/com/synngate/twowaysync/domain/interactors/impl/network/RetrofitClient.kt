package com.synngate.twowaysync.domain.interactors.impl.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getApiService(baseUrl: String): ApiService { // <---- Функция для получения ApiService
        if (retrofit == null || retrofit?.baseUrl().toString() != baseUrl) { // <---- Создаем новый Retrofit, если baseUrl изменился или retrofit еще не инициализирован
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY //  Уровень логирования - BODY (логировать и заголовки и тело запроса/ответа)
            }

            // Создаем TrustManager, который доверяет всем сертификатам
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Создаем SSLContext и инициализируем его нашим TrustManager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Создаем SSLSocketFactory
            val sslSocketFactory = sslContext.socketFactory

            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true } // Пропускаем проверку имени хоста
                .addInterceptor(loggingInterceptor) //  Добавляем Interceptor для логирования (опционально, для отладки)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl) //  Базовый URL сервера будет передаваться в эту функцию
                .addConverterFactory(GsonConverterFactory.create()) //  Gson конвертер для JSON
                .client(client) //  OkHttpClient с Interceptor (для логирования)
                .build()
        }
        return retrofit!!.create(ApiService::class.java) //  Создаем и возвращаем экземпляр ApiService
    }
}
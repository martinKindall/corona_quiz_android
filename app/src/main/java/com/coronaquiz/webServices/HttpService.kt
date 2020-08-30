package com.coronaquiz.webServices

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class HttpService {
    companion object {
        lateinit var client: OkHttpClient

        fun get(): OkHttpClient {
            if (!::client.isInitialized) {
                val timeout = 20L
                client = OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .callTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .build()
            }

            return client
        }
    }
}
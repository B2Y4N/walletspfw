package com.spassdi.walletspfw.data.api

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object RetrofitClient {
    private const val BASE_URL = "qa-diplus.sains.com.my"

    private val certificatePinner = CertificatePinner.Builder()
        .add(BASE_URL, "sha256/R/zhmN4LyfjyueoE72sxL2tM1O0rxP0czNhcpizBQeg=")
        .build()

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://$BASE_URL/DI/api/test/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

internal object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}
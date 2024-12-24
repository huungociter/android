package com.example.scanqrcode.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://vnsystem.smcmfg.com.vn:8491/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson để chuyển đổi JSON
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)  // Tạo API service
}
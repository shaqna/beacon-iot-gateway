package com.maou.beaconiotgateway.di

import com.maou.beaconiotgateway.data.source.service.ApiService
import com.maou.beaconiotgateway.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

val retrofitModule = module {
    single {
        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                httpClient.build()
            ).build()

        retrofit.create(ApiService::class.java)
    }
}
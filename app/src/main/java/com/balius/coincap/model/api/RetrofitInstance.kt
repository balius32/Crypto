package com.balius.coincap.model.api

import android.util.Log
import com.balius.coincap.util.Constants.Companion.LINE_CHART_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        fun createRetrofit(): CoinsApi {
            val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.e("Retrofit", message) // Log the message to logcat
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BASIC // Log only the request URL
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()


            val retrofit = Retrofit.Builder()
                .baseUrl(LINE_CHART_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(CoinsApi::class.java)
        }

    }


}

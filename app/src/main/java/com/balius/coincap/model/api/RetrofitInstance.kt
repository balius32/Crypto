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

           /* val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()*/

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


        /*fun createRetrofit(baseUrl: String): CoinsApi {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(LINE_CHART_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(CoinsApi::class.java)
        }*/


        /*fun createCoinsRetrofit(): CoinsApi {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(LINE_CHART_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(CoinsApi::class.java)
        }

        fun createCandleRetrofit(): CandleApi {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(CANDLE_CHART_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(CandleApi::class.java)
        }*/




   /* companion object {

        fun createCoinsRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(LINE_CHART_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()
        }

        fun createOkHttpClient(): OkHttpClient {
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
            return client

        }
    }*/

}

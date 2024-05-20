package com.balius.coincap.model.api

import com.balius.coincap.model.model.Coins.CoinDetail
import com.balius.coincap.model.model.Coins.Coins
import com.balius.coincap.model.model.chart.CandleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface CoinsApi {

    @GET("v2/assets")
    suspend fun getCoins (): Response<Coins>

    @GET("v2/assets/{id}")
    suspend fun getCoinDetail(
        @Path("id") id: String
    ): CoinDetail



    @GET
    suspend fun getCandles(
        @Url url: String
    ): CandleResponse



}
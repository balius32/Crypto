package com.balius.coincap.model.api

import com.balius.coincap.model.model.CoinDetail
import com.balius.coincap.model.model.Coins
import com.balius.coincap.model.model.chart.CoinChart
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinsApi {

    @GET("assets")
    suspend fun getCoins (): Coins

    @GET("assets/{id}")
    suspend fun getCoinDetail(
        @Path("id") id: String
    ): CoinDetail



    @GET("assets/{id}/history")
    suspend fun getCoinHistory(
        @Path("id") id: String,
        @Query("interval") interval : String = "d1"
    ): CoinChart





}
package com.balius.coincap.model.api

import com.balius.coincap.model.model.Coins
import retrofit2.http.GET

interface CoinsApi {

    @GET("assets")
    suspend fun getCoins (): Coins
}
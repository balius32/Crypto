package com.balius.coincap.model.repository

import android.util.Log
import com.balius.coincap.model.api.CoinsApi
import com.balius.coincap.model.model.Coins
import com.balius.coincap.model.model.Data
import retrofit2.Response

class CoinRepositoryImpl(
    val apiService: CoinsApi
) : CoinRepository {


    override suspend fun getCoins(): List<Data> {
        val request = apiService.getCoins()
        Log.e("coins", request.toString())

        return request.data
    }
}
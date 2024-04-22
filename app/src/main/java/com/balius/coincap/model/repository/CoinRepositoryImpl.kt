package com.balius.coincap.model.repository

import android.util.Log
import com.balius.coincap.model.api.CoinsApi
import com.balius.coincap.model.model.Coins
import com.balius.coincap.model.model.Data
import com.balius.coincap.model.model.chart.ChartData
import retrofit2.Response

class CoinRepositoryImpl(
    val apiService: CoinsApi
) : CoinRepository {


    override suspend fun getCoins(): List<Data> {
        val request = apiService.getCoins()


        return request.data
    }

    override suspend fun getCoinDetail(coinName: String): Data {

        try {
            val request = apiService.getCoinDetail(coinName)
            Log.e("request", request.toString())

            return request.data!!

        } catch (e: Exception) {
            Log.e("req error", e.message.toString())
            return null!!
        }


    }

    override suspend fun getCoinChartData(coinId: String, duration: String): List<ChartData> {
        //todo handle not null
        val request = apiService.getCoinHistory(coinId, duration)
        return request.data
    }
}
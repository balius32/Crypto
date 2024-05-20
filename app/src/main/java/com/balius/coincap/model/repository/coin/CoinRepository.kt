package com.balius.coincap.model.repository.coin

import com.balius.coincap.model.model.Coins.Data
import com.balius.coincap.model.model.chart.CandleChartData

interface CoinRepository {

    suspend fun getCoins () : List<Data>

    suspend fun getCoinDetail (coinName :String) : Data




    suspend fun calculateRSI (prices: List<String>, period: Int) : Double
    suspend fun extractPrices (chartData: List<CandleChartData>) : List<String>
}
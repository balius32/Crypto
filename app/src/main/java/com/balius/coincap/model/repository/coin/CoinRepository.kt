package com.balius.coincap.model.repository.coin

import com.balius.coincap.model.model.Coins.Data
import com.balius.coincap.model.model.chart.candle.CandleChartData
import com.balius.coincap.model.model.chart.line.ChartData

interface CoinRepository {

    suspend fun getCoins () : List<Data>

    suspend fun getCoinDetail (coinName :String) : Data

    suspend fun getCoinChartData (coinId :String, duration : String) : List<ChartData>


    suspend fun calculateRSI (prices: List<String>, period: Int) : Double
    suspend fun extractPrices (chartData: List<ChartData>) : List<String>
}
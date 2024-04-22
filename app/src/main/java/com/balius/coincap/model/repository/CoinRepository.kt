package com.balius.coincap.model.repository

import com.balius.coincap.model.model.Data
import com.balius.coincap.model.model.chart.ChartData

interface CoinRepository {

    suspend fun getCoins () : List<Data>

    suspend fun getCoinDetail (coinName :String) : Data

    suspend fun getCoinChartData (coinId :String, duration : String) : List<ChartData>
}
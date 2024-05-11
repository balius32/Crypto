package com.balius.coincap.model.repository.coin

import android.util.Log
import com.balius.coincap.model.api.CoinsApi
import com.balius.coincap.model.model.Coins.Data
import com.balius.coincap.model.model.chart.candle.CandleChartData
import com.balius.coincap.model.model.chart.line.ChartData
import org.apache.commons.math3.stat.StatUtils

class CoinRepositoryImpl(
    private val apiService: CoinsApi
) : CoinRepository {


    override suspend fun getCoins(): List<Data> {

        val request = apiService.getCoins()
        Log.e("get coin repository", request.toString())

        return if (request.body() != null) {
            request.body()!!.data
        } else {
            emptyList()
        }

    }

    override suspend fun getCoinDetail(coinName: String): Data {

        try {
            val request = apiService.getCoinDetail(coinName)


            return request.data!!

        } catch (e: Exception) {
            Log.e("req error", e.message.toString())
            return null!!
        }


    }

    override suspend fun getCoinChartData(coinId: String, duration: String): List<ChartData> {

        val request = apiService.getCoinHistory(coinId, duration)
        return request.data
    }

    override suspend fun calculateRSI(prices: List<String>, period: Int): Double {


        require(prices.size > period) { "Prices list size must be greater than the RSI period" }

        val doublePrices = prices.map { it.toDoubleOrNull() ?: 0.0 } // Convert prices to Double


        val priceChanges = DoubleArray(doublePrices.size - 1)

        // Calculate price changes
        for (i in 1 until doublePrices.size) {
            priceChanges[i - 1] = doublePrices[i] - doublePrices[i - 1]
        }

        // Separate gains and losses
        val gains = mutableListOf<Double>()
        val losses = mutableListOf<Double>()
        for (i in 0 until priceChanges.size) {
            if (priceChanges[i] > 0) {
                gains.add(priceChanges[i])
            } else {
                losses.add(-priceChanges[i])
            }

        }

        // Calculate average gains and losses
        val avgGain = StatUtils.mean(gains.toDoubleArray())
        val avgLoss = StatUtils.mean(losses.toDoubleArray())

        // Calculate RS (Relative Strength)
        val rs = if (avgLoss == 0.0) {
            Double.POSITIVE_INFINITY
        } else {
            avgGain / avgLoss
        }

        // Calculate RSI
        return 100.0 - (100.0 / (1.0 + rs))
    }

    override suspend fun extractPrices(chartData: List<CandleChartData>): List<String> {
        val prices = mutableListOf<String>()
        for (item in chartData) {
            item.close?.let { price ->
                prices.add(item.close.toString())
            }
        }
        return prices
    }
}
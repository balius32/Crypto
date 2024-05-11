package com.balius.coincap.model.repository.candle

import android.util.Log
import com.balius.coincap.model.api.CoinsApi
import com.balius.coincap.model.model.chart.candle.CandleChartData
import com.balius.coincap.util.Constants.Companion.CANDLE_CHART_BASE_URL


class CandleRepositoryImpl(
    private val apiService: CoinsApi
) : CandleRepository {
    override suspend fun getCandles(symbol: String, from: Long, to: Long, resolution: String): List<CandleChartData>? {


        val url = "${CANDLE_CHART_BASE_URL}?symbol=${symbol}USDT&resolution=60&from=${from}&to=${to}"
        val url1 = "${CANDLE_CHART_BASE_URL}?symbol=${symbol}USDT&resolution=5&from=${from}&to=${to}"
        val url2 = "${CANDLE_CHART_BASE_URL}?symbol=${symbol}USDT&resolution=5&from=${from}&to=${to}"

        try {

            val result = when (resolution) {
                "daily" -> {
                    apiService.getCandles(url)
                }

                "6h" -> {
                    apiService.getCandles(url1)
                }

                "12h" -> {
                    apiService.getCandles(url2)
                }

                else-> null!!
            }

            Log.e("candle", result.toString())

            if (result != null) {
                val candleChartDataList = mutableListOf<CandleChartData>()

                for (i in result.h.indices) {
                    val candleChartData = CandleChartData(
                        high = result.h[i].toFloat(),
                        low = result.l[i].toFloat(),
                        open = result.o[i].toFloat(),
                        close = result.c[i].toFloat()
                    )
                    candleChartDataList.add(candleChartData)
                }
                return candleChartDataList
            } else {
                return emptyList()
            }
        }catch (e: Exception){
            Log.e("error candle", e.message.toString())
            return emptyList()
        }




    }
}
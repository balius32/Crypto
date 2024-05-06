package com.balius.coincap.model.repository.candle

import com.balius.coincap.model.model.chart.candle.CandleChartData

interface CandleRepository {

    suspend fun getCandles(symbol: String, from: Long, to: Long, resolution: String
    ): List<CandleChartData>?
}
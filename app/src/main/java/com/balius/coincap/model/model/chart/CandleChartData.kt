package com.balius.coincap.model.model.chart

import com.google.gson.annotations.SerializedName

data class CandleChartData(
    val high: Float,
    val low: Float,
    val open: Float,
    val close: Float
)
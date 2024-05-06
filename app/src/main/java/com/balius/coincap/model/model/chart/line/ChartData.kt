package com.balius.coincap.model.model.chart.line

import com.google.gson.annotations.SerializedName

data class ChartData(
    @SerializedName("priceUsd") var priceUsd: String? = null,
    @SerializedName("time") var time: Long? = null,
    @SerializedName("circulatingSupply") var circulatingSupply: String? = null,
    @SerializedName("date") var date: String? = null
) {
}
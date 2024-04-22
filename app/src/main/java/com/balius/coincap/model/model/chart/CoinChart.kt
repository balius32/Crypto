package com.balius.coincap.model.model.chart

import com.google.gson.annotations.SerializedName

data class CoinChart(

    @SerializedName("data"      ) var data      : ArrayList<ChartData> = arrayListOf(),
    @SerializedName("timestamp" ) var timestamp : Long?            = null
)
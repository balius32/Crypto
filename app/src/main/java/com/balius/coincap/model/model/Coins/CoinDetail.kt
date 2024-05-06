package com.balius.coincap.model.model.Coins

import com.google.gson.annotations.SerializedName

data class CoinDetail(
    @SerializedName("data"      ) var data      : Data? = Data(),
    @SerializedName("timestamp" ) var timestamp : Long?  = null
)

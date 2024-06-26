package com.balius.coincap.model.model.Coins

import com.google.gson.annotations.SerializedName

data class Coins(

    @SerializedName("data"      ) var data      : ArrayList<Data> = arrayListOf(),
    @SerializedName("timestamp" ) var timestamp : Long?            = null
)
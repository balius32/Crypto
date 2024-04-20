package com.balius.coincap.model.repository

import com.balius.coincap.model.model.Data

interface CoinRepository {

    suspend fun getCoins () : List<Data>
}
package com.balius.coincap.ui.coinDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balius.coincap.model.model.Coins.Data
import com.balius.coincap.model.model.chart.CandleChartData
import com.balius.coincap.model.repository.candle.CandleRepository
import com.balius.coincap.model.repository.coin.CoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinDetailViewModel(
    private val repository: CoinRepository,
    private val candleRepository: CandleRepository
) : ViewModel() {

    private val _detail = MutableLiveData<Data>()
    val detail: LiveData<Data>
        get() = _detail

    private val _rsi = MutableLiveData<Double>()
    val rsi: LiveData<Double>
        get() = _rsi

    private val _prices = MutableLiveData<List<String>>()
    val prices: LiveData<List<String>>
        get() = _prices


    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean>
        get() = _isError


    private val _candleData = MutableLiveData<List<CandleChartData>>()
    val candleData: LiveData<List<CandleChartData>>
        get() = _candleData


    fun getDetails(coinName: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val coinDetail = repository.getCoinDetail(coinName)
                    coinDetail
                }
                Log.e("result", result.toString())
                if (result.maxSupply==null){
                    result.maxSupply = "_"
                    _detail.value = result
                }else{
                    _detail.value = result
                }


            } catch (e: Exception) {
                Log.e("detail error", e.message.toString())
                _isError.value = true

            }
        }
    }

    fun calculateRSI(prices: List<String>, period: Int) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val rsii = repository.calculateRSI(prices, period)
                    rsii
                }
                _rsi.value = result

            } catch (e: Exception) {
                Log.e("rsi error", e.message.toString())
            }

        }
    }

    fun getPricesList(data: List<CandleChartData>) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val stringData = repository.extractPrices(data)
                    stringData
                }
                _prices.value = result
            } catch (e: Exception) {
                Log.e("cast to string", e.message.toString())
            }
        }

    }
    fun getCandles(symbol: String, from: Long, to: Long, resolution: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val candl = candleRepository.getCandles(symbol,from,to,resolution)
                    candl
                }
                if (result != null){
                    if (result.isNotEmpty())
                        _candleData.value = result!!
                }else{
                    _isError.value = true
                }


            } catch (e: Exception) {
                _isError.value = true
                Log.e("candle error", e.message.toString())
            }

        }
    }


}
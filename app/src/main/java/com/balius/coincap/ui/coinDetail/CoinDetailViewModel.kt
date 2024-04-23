package com.balius.coincap.ui.coinDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balius.coincap.model.model.Data
import com.balius.coincap.model.model.chart.ChartData
import com.balius.coincap.model.repository.CoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinDetailViewModel(
    private val repository: CoinRepository
) : ViewModel() {

    private val _detail = MutableLiveData<Data>()
    val detail: LiveData<Data>
        get() = _detail

    private val _chartData = MutableLiveData<List<ChartData>>()
    val chartData: LiveData<List<ChartData>>
        get() = _chartData


    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean>
        get() = _isError


    fun getDetails(coinName: String) {
        viewModelScope.launch {
            try {


                val result = withContext(Dispatchers.IO) {
                    val coinDetail = repository.getCoinDetail(coinName)
                    coinDetail
                }
                _detail.value = result

            } catch (e: Exception) {
                _isError.value = true

            }
        }
    }


    fun getChartDetail(coinName: String, duration: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val chart = repository.getCoinChartData(coinName, duration)
                    chart
                }
                _chartData.value = result

            } catch (e: Exception) {
               _isError.value = true
            }
        }
    }


}
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



    fun getDetails(coinName: String) {
        viewModelScope.launch {
            try {
                Log.e("detail Error", "hi1")

                val result = withContext(Dispatchers.IO){
                   val coinDetail= repository.getCoinDetail(coinName)
                    coinDetail
                }
                _detail.value = result

            } catch (e: Exception) {

                Log.e("detail Error", e.message.toString())

            }
        }
    }


    fun getChartDetail(coinName: String,duration : String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){
                    val chart= repository.getCoinChartData(coinName,duration)
                    chart
                }
                    _chartData.value = result

            } catch (e: Exception) {
                //todo handle error

                Log.e("chart data Error", e.message.toString())

            }
        }
    }


}
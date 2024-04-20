package com.balius.coincap.ui.coins

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balius.coincap.model.model.Data
import com.balius.coincap.model.repository.CoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinsViewModel(
    private val repository: CoinRepository
) : ViewModel() {

    private val _Coins = MutableLiveData<List<Data>>()
    val coins : LiveData<List<Data>>
        get() = _Coins

    init {
        getCoins()
    }


    fun getCoins(){
        viewModelScope.launch {
            try {
                val result =  withContext(Dispatchers.IO){
                    val data =repository.getCoins()
                    data
                }
                Log.e("data", result.toString())
                _Coins.value = result

            }catch (e : Exception){
                Log.e("data error", e.message.toString())
            }

        }
    }
}
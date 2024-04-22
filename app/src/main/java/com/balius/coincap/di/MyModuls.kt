package com.balius.coincap.di

import com.balius.coincap.model.api.RetrofitInstance
import com.balius.coincap.model.repository.CoinRepository
import com.balius.coincap.model.repository.CoinRepositoryImpl
import com.balius.coincap.ui.coinDetail.CoinDetailViewModel
import com.balius.coincap.ui.coins.CoinsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val MyModules = module {

    single { RetrofitInstance.createRetrofit() }

    single<CoinRepository> { CoinRepositoryImpl(get()) }


    viewModel { CoinsViewModel(get()) }
    viewModel { CoinDetailViewModel(get()) }

}
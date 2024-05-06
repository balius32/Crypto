package com.balius.coincap.di

import com.balius.coincap.model.api.RetrofitInstance
import com.balius.coincap.model.repository.candle.CandleRepository
import com.balius.coincap.model.repository.candle.CandleRepositoryImpl
import com.balius.coincap.model.repository.coin.CoinRepository
import com.balius.coincap.model.repository.coin.CoinRepositoryImpl
import com.balius.coincap.ui.coinDetail.CoinDetailViewModel
import com.balius.coincap.ui.coins.CoinsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val MyModules = module {

    single { RetrofitInstance.createRetrofit() }

    single<CoinRepository> { CoinRepositoryImpl(get()) }
    single<CandleRepository> { CandleRepositoryImpl(get()) }


    viewModel { CoinsViewModel(get()) }
    viewModel { CoinDetailViewModel(get(), get()) }
}

//single { RetrofitInstance.createCoinsRetrofit() }


/*
val candleModule = module {
    single { createCandleRetrofit() }
    factory<CandleApi> { get<Retrofit>().create(CandleApi::class.java) }
    single<CandleRepository> { CandleRepositoryImpl(get()) }
}
val coinsModule = module {
    single { createCoinsRetrofit() }
    factory<CoinsApi> { get<Retrofit>().create(CoinsApi::class.java) }
    single<CoinRepository> { CoinRepositoryImpl(get()) }
}
val viewModelModule = module {
    viewModel { CoinsViewModel(get()) }
    viewModel { CoinDetailViewModel(get(), get()) }

}

// Combine modules
val myModules = listOf(candleModule,coinsModule, viewModelModule)
*/

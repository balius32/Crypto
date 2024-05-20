package com.balius.coincap.application

import android.app.Application
import android.content.Context
import com.balius.coincap.di.MyModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class MyApplication: Application() {
    companion object {
        lateinit var appContext: Context
    }
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        GlobalContext.startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(MyModules)
        }

    }

}
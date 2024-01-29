package com.lazycode.trafficsense

import android.app.Application
import com.lazycode.trafficsense.di.dataStoreModule
import com.lazycode.trafficsense.di.networkModule
import com.lazycode.trafficsense.di.repositoryModule
import com.lazycode.trafficsense.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TrafficSense : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@TrafficSense)
            modules(
                listOf(
                    networkModule,
                    dataStoreModule,
                    repositoryModule,
                    viewModelModule
                )
            )
        }
    }
}
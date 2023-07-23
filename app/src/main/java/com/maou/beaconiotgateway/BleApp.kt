package com.maou.beaconiotgateway

import android.app.Application
import com.maou.beaconiotgateway.di.dataModule
import com.maou.beaconiotgateway.di.domainModule
import com.maou.beaconiotgateway.di.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class BleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BleApp)
            loadKoinModules(
                listOf(
                    dataModule,
                    domainModule,
                    retrofitModule
                )
            )
        }
    }
}
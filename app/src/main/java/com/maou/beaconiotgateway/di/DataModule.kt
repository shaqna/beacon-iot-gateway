package com.maou.beaconiotgateway.di

import com.maou.beaconiotgateway.data.controller.BluetoothLeControllerImpl
import com.maou.beaconiotgateway.data.repository.BeaconRepositoryImpl
import com.maou.beaconiotgateway.domain.controller.BluetoothLeController
import com.maou.beaconiotgateway.domain.repository.BeaconRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::BluetoothLeControllerImpl){
        bind<BluetoothLeController>()
    }

    singleOf(::BeaconRepositoryImpl) {
        bind<BeaconRepository>()
    }
}


package com.maou.beaconiotgateway.di

import com.maou.beaconiotgateway.domain.usecase.BeaconInteractor
import com.maou.beaconiotgateway.domain.usecase.BeaconUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::BeaconInteractor) {
        bind<BeaconUseCase>()
    }
}
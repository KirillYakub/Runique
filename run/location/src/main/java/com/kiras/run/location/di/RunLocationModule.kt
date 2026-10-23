package com.kiras.run.location.di

import com.kiras.run.domain.LocationObserver
import com.kiras.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runLocationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}
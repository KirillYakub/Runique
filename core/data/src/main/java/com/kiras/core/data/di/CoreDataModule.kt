package com.kiras.core.data.di

import com.kiras.core.data.auth.EncryptedSessionStorage
import com.kiras.core.data.run.LocalRunRepository
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.run.RunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()

    singleOf(::LocalRunRepository).bind<RunRepository>()
}

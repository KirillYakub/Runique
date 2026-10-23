package com.kiras.auth.data.di

import com.kiras.auth.data.EmailPatternValidator
import com.kiras.auth.data.PasswordHasher
import com.kiras.auth.data.RoomAuthRepository
import com.kiras.auth.domain.AuthRepository
import com.kiras.auth.domain.PatternValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::PasswordHasher)
    singleOf(::RoomAuthRepository).bind<AuthRepository>()
}

package com.kiras.auth.presentation.di

import com.kiras.auth.domain.usecase.ValidateEmailUseCase
import com.kiras.auth.domain.usecase.ValidatePasswordUseCase
import com.kiras.auth.presentation.login.LoginViewModel
import com.kiras.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    factoryOf(::ValidateEmailUseCase)
    factoryOf(::ValidatePasswordUseCase)

    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}

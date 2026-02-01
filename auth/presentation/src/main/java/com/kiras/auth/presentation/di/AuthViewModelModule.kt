package com.kiras.auth.presentation.di

import com.kiras.auth.presentation.login.LoginViewModel
import com.kiras.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}
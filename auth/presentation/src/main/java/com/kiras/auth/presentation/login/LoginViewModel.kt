package com.kiras.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.auth.domain.AuthRepository
import com.kiras.auth.domain.usecase.ValidateEmailUseCase
import com.kiras.auth.presentation.util.textAsFlow
import com.kiras.core.domain.util.Result
import com.kiras.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        combine(
            flow = state.email.textAsFlow(),
            flow2 = state.password.textAsFlow()
        ) { email, password ->
            state = state.copy(
                canLogin = validateEmail(email.toString().trim()) &&
                    password.isNotEmpty()
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: LoginAction) {
        when(action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnRegisterClick -> Unit
            LoginAction.OnTogglePasswordVisibility -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLoggingIn = true)
            val result = authRepository.login(
                email = state.email.text.toString().trim(),
                password = state.password.text.toString(),
            )
            state = state.copy(isLoggingIn = false)
            when(result) {
                is Result.Error -> {
                    eventChannel.send(LoginEvent.Error(result.error.asUiText()))
                }
                is Result.Success -> {
                    eventChannel.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }
}
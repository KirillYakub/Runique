package com.kiras.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.auth.domain.AuthRepository
import com.kiras.auth.domain.usecase.ValidateEmailUseCase
import com.kiras.auth.domain.usecase.ValidatePasswordUseCase
import com.kiras.auth.presentation.util.textAsFlow
import com.kiras.core.domain.util.Result
import com.kiras.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        state.username.textAsFlow().onEach { username ->
            state = state.copy(isUsernameValid = username.isNotBlank())
            updateCanRegister()
        }.launchIn(viewModelScope)

        state.email.textAsFlow().onEach { email ->
            state = state.copy(isEmailValid = validateEmail(email.toString()))
            updateCanRegister()
        }.launchIn(viewModelScope)

        state.password.textAsFlow().onEach { password ->
            state = state.copy(passwordValidationState = validatePassword(password.toString()))
            updateCanRegister()
        }.launchIn(viewModelScope)
    }


    fun onAction(action: RegisterAction) {
        when(action) {
            RegisterAction.OnLoginClick -> Unit
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
        }
    }

    private fun updateCanRegister() {
        state = state.copy(
            canRegister = state.isUsernameValid &&
                    state.isEmailValid &&
                    state.passwordValidationState.isValidPassword &&
                    !state.isRegistering
        )
    }

    private fun register() {
        viewModelScope.launch {
            state = state.copy(isRegistering = true)
            val result = authRepository.register(
                email = state.email.text.toString().trim(),
                username = state.username.text.toString().trim(),
                password = state.password.text.toString()
            )
            state = state.copy(isRegistering = false)
            when(result) {
                is Result.Error -> {
                    eventChannel.send(RegisterEvent.Error(result.error.asUiText()))
                }
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }
            }
        }
    }
}

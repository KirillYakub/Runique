package com.kiras.auth.domain.usecase

import com.kiras.auth.domain.PasswordValidationState

class ValidatePasswordUseCase {

    operator fun invoke(password: String): PasswordValidationState {
        return PasswordValidationState(
            hasMinLength = password.length >= MIN_PASSWORD_LENGTH,
            hasNumber = password.any { it.isDigit() },
            hasLowerCaseCharacter = password.any { it.isLowerCase() },
            hasUpperCaseCharacter = password.any { it.isUpperCase() }
        )
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}

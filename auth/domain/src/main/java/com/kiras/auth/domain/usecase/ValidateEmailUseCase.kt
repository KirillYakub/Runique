package com.kiras.auth.domain.usecase

import com.kiras.auth.domain.PatternValidator

class ValidateEmailUseCase(
    private val patternValidator: PatternValidator
) {
    operator fun invoke(email: String): Boolean = patternValidator.matches(email.trim())
}

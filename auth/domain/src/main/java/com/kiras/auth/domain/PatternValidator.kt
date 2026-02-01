package com.kiras.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}
package com.tminus1010.buva.domain

// Left as a sealed class instead of an enum, in case it needs to have polymorphism later on.
sealed class ValidationResult {
    object Success : ValidationResult()
    object Warning : ValidationResult()
    object Failure : ValidationResult()
}

val ValidationResult.isFailure get() = this == ValidationResult.Failure

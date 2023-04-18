package com.tminus1010.buva.domain

sealed class ValidationResult {
    object Success : ValidationResult()
    class Warning(e: Throwable? = null) : ValidationResult()
    class Failure(e: Throwable? = null) : ValidationResult()
}

val ValidationResult.isFailure get() = this is ValidationResult.Failure

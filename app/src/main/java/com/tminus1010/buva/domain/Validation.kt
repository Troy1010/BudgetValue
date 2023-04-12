package com.tminus1010.buva.domain

// Left as a sealed class instead of an enum, in case it needs to have polymorphism later on.
sealed class Validation {
    object Success : Validation()
    object Warning : Validation()
    object Failure : Validation()
}

package com.tminus1010.budgetvalue.all_features.presentation

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Errors private constructor(private val errors: MutableSharedFlow<Throwable>) : MutableSharedFlow<Throwable> by errors {
    @Inject
    constructor() : this(MutableSharedFlow<Throwable>())
}
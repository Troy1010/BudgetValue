package com.tminus1010.buva.ui.errors

import com.tminus1010.buva.all_layers.extensions.onNext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Errors private constructor(private val errors: MutableSharedFlow<Throwable>) : MutableSharedFlow<Throwable> by errors {
    @Inject
    constructor() : this(MutableSharedFlow<Throwable>())

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, e -> errors.onNext(e) }
    val globalScope = GlobalScope + coroutineExceptionHandler
}
package com.tminus1010.buva.ui.errors

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Errors private constructor(
    private val errorSubject: MutableSharedFlow<Throwable>,
    val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, e -> runBlocking { errorSubject.emit(e) } },
) : MutableSharedFlow<Throwable> by errorSubject, CoroutineExceptionHandler by coroutineExceptionHandler {
    @Inject
    constructor() : this(MutableSharedFlow<Throwable>())

    val globalScope = GlobalScope + coroutineExceptionHandler
}
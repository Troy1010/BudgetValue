package com.tminus1010.budgetvalue._core.framework.view

import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpinnerService @Inject constructor() {
    // # Input
    fun <T> decorate(flow: Flow<T>) = flow.onStart { asyncTaskStarted.emit(Unit) }.onCompletion { asyncTaskEnded.emit(Unit) }
    fun decorate(completable: Completable) = completable.doOnSubscribe { runBlocking { asyncTaskStarted.emit(Unit) } }.doOnTerminate { runBlocking { asyncTaskEnded.emit(Unit) } }
    suspend fun asyncTaskStarted() {
        asyncTaskStarted.emit(Unit)
    }

    suspend fun asyncTaskEnded() {
        asyncTaskEnded.emit(Unit)
    }

    // # Internal
    private val asyncTaskStarted = MutableSharedFlow<Unit>()
    private val asyncTaskEnded = MutableSharedFlow<Unit>()

    // # State
    val isSpinnerVisible =
        merge(asyncTaskStarted.map { 1 }, asyncTaskEnded.map { -1 })
            .scan(0) { acc, v -> acc + v }
            .map { it != 0 }
            .distinctUntilChanged()
}
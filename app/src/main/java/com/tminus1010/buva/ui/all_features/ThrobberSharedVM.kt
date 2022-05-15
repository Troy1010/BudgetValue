package com.tminus1010.buva.ui.all_features

import android.view.View
import com.tminus1010.tmcommonkotlin.coroutines.IJobEvents
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThrobberSharedVM @Inject constructor() : IJobEvents {
    // # Input
    fun <T> decorate(flow: Flow<T>) = flow.onStart { asyncTaskStarted.emit(Unit) }.onCompletion { asyncTaskEnded.emit(Unit) }
    fun decorate(completable: Completable) = completable.doOnSubscribe { runBlocking { asyncTaskStarted.emit(Unit) } }.doOnTerminate { runBlocking { asyncTaskEnded.emit(Unit) } }
    fun decorate(lambda: suspend CoroutineScope.() -> Unit): suspend CoroutineScope.() -> Unit = {
        asyncTaskStarted.emit(Unit)
        lambda(this)
        asyncTaskEnded.emit(Unit)
    }

    suspend fun asyncTaskStarted() {
        asyncTaskStarted.emit(Unit)
    }

    suspend fun asyncTaskEnded() {
        asyncTaskEnded.emit(Unit)
    }

    override fun onStart() {
        runBlocking { asyncTaskStarted.emit(Unit) }
    }

    override fun onComplete() {
        runBlocking { asyncTaskEnded.emit(Unit) }
    }

    // # Internal
    private val asyncTaskStarted = MutableSharedFlow<Unit>()
    private val asyncTaskEnded = MutableSharedFlow<Unit>()

    // # State
    /**
     * Requirement: Given task is short Then do not show spinner.
     */
    val visibility =
        merge(asyncTaskStarted.map { 1 }, asyncTaskEnded.map { -1 })
            .scan(0) { acc, v -> acc + v }
            .map { if (it == 0) View.GONE else View.VISIBLE }
            .onEach { if (it == View.VISIBLE) delay(250) } // TODO: This might cause bugs..
            .stateIn(GlobalScope, SharingStarted.Eagerly, View.GONE)
}
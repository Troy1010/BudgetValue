package com.tminus1010.buva.ui.all_features

import android.view.View
import com.tminus1010.tmcommonkotlin.coroutines.IJobEvents
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThrobberSharedVM @Inject constructor() : IJobEvents {
    // # Input
    fun <T> decorate(flow: Flow<T>): Flow<T> {
        val started = AtomicBoolean(false)
        return flow
            .onStart {
                if (started.compareAndSet(false, true))
                    asyncTaskStarted.emit(Unit)
            }
            .onEach {
                if (started.compareAndSet(true, false))
                    asyncTaskEnded.emit(Unit)
            }
            .onCompletion {
                if (started.compareAndSet(true, false))
                    asyncTaskEnded.emit(Unit)
            }
    }

    fun decorate(completable: Completable) = completable.doOnSubscribe { runBlocking { asyncTaskStarted.emit(Unit) } }.doOnTerminate { runBlocking { asyncTaskEnded.emit(Unit) } }

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

    // # Private
    private val asyncTaskStarted = MutableSharedFlow<Unit>()
    private val asyncTaskEnded = MutableSharedFlow<Unit>()

    // # State
    val visibility =
        merge(asyncTaskStarted.map { 1 }, asyncTaskEnded.map { -1 })
            .scan(0) { acc, v -> acc + v }
            .map { if (it == 0) View.GONE else View.VISIBLE }
            .stateIn(GlobalScope, SharingStarted.Eagerly, View.GONE)
}
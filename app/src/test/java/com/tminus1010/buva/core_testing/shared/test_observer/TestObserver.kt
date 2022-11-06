package com.tminus1010.buva.core_testing.shared.test_observer

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class TestObserver<T>(flow: Flow<T>) {
    val x = flow.scan(listOf<T>()) { acc, v -> acc + v }
        .stateIn(GlobalScope, SharingStarted.Eagerly, listOf()) // TODO: Is there a way to use a test scope instead of GlobalScope..?

    fun values(): List<T> {
        return x.value
    }
}
package com.tminus1010.buva.core_testing.shared.test_observer

import kotlinx.coroutines.flow.Flow

fun <T : Any> Flow<T>.test() = TestObserver(this)
package com.tminus1010.buva.all_layers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, R : Any> combine(flow1: Flow<T1>, flow2: Flow<T2>, flow3: Flow<T3>, flow4: Flow<T4>, flow5: Flow<T5>, flow6: Flow<T6>, transform: suspend (T1, T2, T3, T4, T5, T6) -> R): Flow<R> {
    val a = combine(flow1, flow2, flow3, ::Triple)
    val b = combine(flow4, flow5, flow6, ::Triple)
    return combine(a, b) { firstTriple, secondTriple -> transform(firstTriple.first, firstTriple.second, firstTriple.third, secondTriple.first, secondTriple.second, secondTriple.third) }
}
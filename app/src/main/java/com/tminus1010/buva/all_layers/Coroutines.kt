@file:Suppress("UNCHECKED_CAST", "PackageDirectoryMismatch")

package kotlinx.coroutines.flow

fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, R : Any> combine(flow1: Flow<T1>, flow2: Flow<T2>, flow3: Flow<T3>, flow4: Flow<T4>, flow5: Flow<T5>, flow6: Flow<T6>, transform: suspend (T1, T2, T3, T4, T5, T6) -> R): Flow<R> {
    return combine(arrayListOf(flow1, flow2, flow3, flow4, flow5, flow6)) { x: Array<Any> -> transform(x[0] as T1, x[1] as T2, x[2] as T3, x[3] as T4, x[4] as T5, x[5] as T6) }
}
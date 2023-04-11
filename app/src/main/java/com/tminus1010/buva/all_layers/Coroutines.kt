@file:Suppress("UNCHECKED_CAST", "PackageDirectoryMismatch")

package kotlinx.coroutines.flow

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * A convenience method which is a little easier to use.
 */
fun onError(lambda: (Throwable) -> Unit): CoroutineContext {
    return CoroutineExceptionHandler { _, exception -> lambda(exception) }
}
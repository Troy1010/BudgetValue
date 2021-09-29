package com.tminus1010.budgetvalue.all.framework.extensions

import io.reactivex.rxjava3.subjects.Subject

operator fun <T> Subject<T>.invoke(value: T) =
    onNext(value)

operator fun Subject<Unit>.invoke() =
    onNext(Unit)

fun Subject<Unit>.emit() =
    onNext(Unit)

/**
 * Useful for:
 *     button.setOnClickListener(subject::onNext2)
 */
fun Subject<Unit>.onNext2(any: Any) =
    onNext(Unit)

fun Subject<Unit>.onNext2() =
    onNext(Unit)
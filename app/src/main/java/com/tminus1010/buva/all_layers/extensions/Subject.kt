package com.tminus1010.buva.all_layers.extensions

import io.reactivex.rxjava3.subjects.Subject

operator fun <T> Subject<T>.invoke(value: T) =
    onNext(value)

operator fun Subject<Unit>.invoke() =
    onNext(Unit)
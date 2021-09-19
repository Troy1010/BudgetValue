package com.tminus1010.budgetvalue.all.framework.extensions

import io.reactivex.rxjava3.subjects.PublishSubject

fun PublishSubject<Unit>.emit() =
    onNext(Unit)
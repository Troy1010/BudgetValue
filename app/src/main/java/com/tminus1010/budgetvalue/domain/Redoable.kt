package com.tminus1010.budgetvalue.domain

import io.reactivex.rxjava3.core.Completable

data class Redoable(
    val redo: Completable,
    val undo: Completable
)

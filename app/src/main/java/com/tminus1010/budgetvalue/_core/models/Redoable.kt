package com.tminus1010.budgetvalue._core.models

import io.reactivex.rxjava3.core.Completable

data class Redoable(
    val redo: Completable,
    val undo: Completable
)

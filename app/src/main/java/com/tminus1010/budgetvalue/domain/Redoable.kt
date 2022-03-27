package com.tminus1010.budgetvalue.domain

data class Redoable(
    val redo: suspend () -> Unit,
    val undo: suspend () -> Unit,
)

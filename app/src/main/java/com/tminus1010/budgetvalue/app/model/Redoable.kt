package com.tminus1010.budgetvalue.app.model

data class Redoable(
    val redo: suspend () -> Unit,
    val undo: suspend () -> Unit,
)

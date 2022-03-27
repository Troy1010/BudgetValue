package com.tminus1010.budgetvalue.app.model

data class RedoUndo(
    val redo: suspend () -> Unit,
    val undo: suspend () -> Unit,
)

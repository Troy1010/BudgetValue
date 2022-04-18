package com.tminus1010.buva.app.model

data class RedoUndo(
    val redo: suspend () -> Unit,
    val undo: suspend () -> Unit,
)

package com.tminus1010.budgetvalue.app

import com.tminus1010.budgetvalue.app.model.RedoUndo
import com.tminus1010.budgetvalue.framework.observable.source_objects.SourceList
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedoUndoInteractor @Inject constructor() {
    // # Input
    suspend fun useAndAdd(redoUndo: RedoUndo) {
        redoUndo.redo()
        undoQueue.add(redoUndo)
    }

    suspend fun undo() {
        undoQueue.takeLast()
            ?.also { redoQueue.add(it) }
            ?.also { it.undo() }
    }

    suspend fun redo() {
        redoQueue.takeLast()
            ?.also { undoQueue.add(it) }
            ?.also { it.redo() }
    }

    // # Internal
    private val undoQueue = SourceList<RedoUndo>()
    private val redoQueue = SourceList<RedoUndo>()

    // # Output
    val isUndoAvailable = undoQueue.flow.map { it.isNotEmpty() }
    val isRedoAvailable = redoQueue.flow.map { it.isNotEmpty() }
}
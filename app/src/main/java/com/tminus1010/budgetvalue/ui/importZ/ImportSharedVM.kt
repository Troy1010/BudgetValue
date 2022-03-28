package com.tminus1010.budgetvalue.ui.importZ

import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportSharedVM @Inject constructor() {
    // # User Intents
    fun userTryNavToSelectFile() {
        navToSelectFile.onNext()
    }

    // # Events
    val navToSelectFile = MutableSharedFlow<Unit>()
}
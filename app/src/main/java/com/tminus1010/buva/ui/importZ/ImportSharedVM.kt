package com.tminus1010.buva.ui.importZ

import com.tminus1010.buva.all_layers.extensions.onNext
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
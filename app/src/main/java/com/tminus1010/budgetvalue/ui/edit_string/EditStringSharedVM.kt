package com.tminus1010.budgetvalue.ui.edit_string

import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditStringSharedVM @Inject constructor() {
    // # Setup
    lateinit var initialS: String

    // # User Intents
    val userSubmitString = MutableSharedFlow<String>()
    init {
        userSubmitString.observe(GlobalScope) { logz("userSubmitString:$it") }
    }
    val userCancel = MutableSharedFlow<Unit>()
}
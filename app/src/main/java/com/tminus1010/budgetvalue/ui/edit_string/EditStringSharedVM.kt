package com.tminus1010.budgetvalue.ui.edit_string

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditStringSharedVM @Inject constructor() {
    // # Setup
    lateinit var initialS: String

    // # User Intents
    val userSubmitString = MutableSharedFlow<String>()
    val userCancel = MutableSharedFlow<Unit>()
}
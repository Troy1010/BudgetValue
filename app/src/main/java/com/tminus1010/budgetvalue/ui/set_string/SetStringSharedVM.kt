package com.tminus1010.budgetvalue.ui.set_string

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetStringSharedVM @Inject constructor() {
    // # Setup
    lateinit var initialS: String

    // # User Intents
    val userSubmitString = MutableSharedFlow<String>()
    val userCancel = MutableSharedFlow<Unit>()
}
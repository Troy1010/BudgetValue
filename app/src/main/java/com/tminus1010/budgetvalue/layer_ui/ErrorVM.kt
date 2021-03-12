package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.layer_ui.misc.ButtonPartial
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorVM @Inject constructor() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonPartial>())
}
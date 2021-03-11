package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.layer_ui.misc.ButtonPartial
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM : ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonPartial>())
}
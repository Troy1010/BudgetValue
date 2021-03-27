package com.tminus1010.budgetvalue.aa_shared

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.aa_core.middleware.ui.ButtonPartial
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM: ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonPartial>())
}
package com.tminus1010.budgetvalue.features_shared

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.middleware.ui.ButtonPartial
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM: ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonPartial>())
}
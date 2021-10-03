package com.tminus1010.budgetvalue._core.presentation_and_view.error

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM: ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonVMItem>())
}
package com.tminus1010.budgetvalue.ui.errors

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM: ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonVMItem>())
}
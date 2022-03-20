package com.tminus1010.budgetvalue.all_features.presentation.view_model

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.presentation.model.ButtonVMItem
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM: ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonVMItem>())
}
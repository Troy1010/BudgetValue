package com.tminus1010.budgetvalue._core

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM: ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonRVItem>())
}
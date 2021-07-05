package com.tminus1010.budgetvalue.budgeted

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class BudgetedVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    budgetedDomain: BudgetedDomain,
) : ViewModel() {
    // # Output
    val defaultAmount: Observable<String> = budgetedDomain.defaultAmount
        .map { it.toString() }
        .divertErrors(errorSubject)
    val categoryAmounts = budgetedDomain.categoryAmountsObservableMap
        .map { it.mapValues { it.value.map { it.toString() }.divertErrors(errorSubject) } }
        .divertErrors(errorSubject)
}
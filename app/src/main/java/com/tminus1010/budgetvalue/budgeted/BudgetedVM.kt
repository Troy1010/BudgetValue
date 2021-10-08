package com.tminus1010.budgetvalue.budgeted

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.divertErrors
import com.tminus1010.budgetvalue._core.all.extensions.isPositive
import com.tminus1010.budgetvalue._core.presentation.model.ValidatedStringVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class BudgetedVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    budgetedInteractor: BudgetedInteractor,
) : ViewModel() {
    // # Output
    val defaultAmount: Observable<String> = budgetedInteractor.defaultAmount
        .map { it.toString() }
        .divertErrors(errorSubject)
    val categoryAmounts = budgetedInteractor.categoryAmountsObservableMap

    val categoryValidatedStringVMItems =
        categoryAmounts.map {
            it.mapValues { it.value.map { ValidatedStringVMItem(it, BigDecimal::isPositive) } }
        }
}
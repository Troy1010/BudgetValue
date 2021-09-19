package com.tminus1010.budgetvalue.budgeted

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.isPositive
import com.tminus1010.budgetvalue.all.presentation_and_view._models.ValidatedStringVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
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

    val categoryValidatedStringVMItems =
        categoryAmounts.map {
            it.mapValues { it.value.map { ValidatedStringVMItem(it, BigDecimal::isPositive) } }
        }
}
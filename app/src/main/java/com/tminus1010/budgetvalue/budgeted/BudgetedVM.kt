package com.tminus1010.budgetvalue.budgeted

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.budgeted.domain.BudgetedDomain
import com.tminus1010.budgetvalue.budgeted.domain.IBudgetedDomain
import com.tminus1010.budgetvalue.budgeted.models.Budgeted
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class BudgetedVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    budgetedDomain: BudgetedDomain,
) : ViewModel() {
    // # State
    val defaultAmount: LiveData<String> = budgetedDomain.defaultAmount
        .map { it.toString() }
        .toLiveData(errorSubject)
    val categoryAmounts: LiveData<Map<Category, LiveData<String>>> =
        budgetedDomain.categoryAmountsObservableMap
            .map { it.mapValues { it.value.map { it.toString() }.toLiveData(errorSubject) } }
            .toLiveData(errorSubject)
}
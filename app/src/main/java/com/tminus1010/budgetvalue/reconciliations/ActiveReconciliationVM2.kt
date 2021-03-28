package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

// Separate from ActiveReconciliationVM to avoid circular dependency graph
@HiltViewModel
class ActiveReconciliationVM2 @Inject constructor(
    activeReconciliationDomain2: ActiveReconciliationDomain2
) : ViewModel(), IActiveReconciliationDomain2 by activeReconciliationDomain2
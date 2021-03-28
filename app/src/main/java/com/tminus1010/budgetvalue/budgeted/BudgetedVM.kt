package com.tminus1010.budgetvalue.budgeted

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.accounts.AccountsVM
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.reconciliations.ActiveReconciliationVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.accounts.AccountsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BudgetedVM @Inject constructor(
    budgetedDomain: BudgetedDomain
): ViewModel(), IBudgetedDomain by budgetedDomain
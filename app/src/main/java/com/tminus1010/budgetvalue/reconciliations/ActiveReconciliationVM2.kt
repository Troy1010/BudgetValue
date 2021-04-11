package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.await
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain2
import com.tminus1010.budgetvalue.reconciliations.domain.ReconciliationDomain
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

// Separate from ActiveReconciliationVM to avoid circular dependency graph
@HiltViewModel
class ActiveReconciliationVM2 @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val activeReconciliationDomain: ActiveReconciliationDomain,
    private val activeReconciliationDomain2: ActiveReconciliationDomain2,
    private val reconciliationsRepo: IReconciliationsRepo,
) : ViewModel() {
    // # State
    val defaultAmount: LiveData<String> = activeReconciliationDomain2.defaultAmount
        .map { it.toString() }
        .toLiveData(errorSubject)

    // # Intents
    fun saveReconciliation() {
        Rx.launch {
            Reconciliation(
                LocalDate.now(),
                activeReconciliationDomain2.defaultAmount.await(),
                activeReconciliationDomain.activeReconcileCAs.await().filter { it.value != BigDecimal(0) },
            )
                .let { reconciliationsRepo.push(it) }
                .andThen(reconciliationsRepo.clearActiveReconcileCAs())
        }
    }
}
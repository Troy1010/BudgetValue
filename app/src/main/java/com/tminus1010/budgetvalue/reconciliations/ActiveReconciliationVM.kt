package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.await
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDomain2
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActiveReconciliationVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val reconciliationsRepo: IReconciliationsRepo,
    categoriesDomain: CategoriesDomain,
    private val activeReconciliationDomain2: ActiveReconciliationDomain2,
) : ViewModel() {
    // # State
    val activeReconcileCAs2 =
        Rx.combineLatest(reconciliationsRepo.activeReconciliationCAs, categoriesDomain.userCategories)
            // These extra zeros prevent refreshes on hidden additions/removals that happen when a value is set to 0.
            .map { (activeReconcileCAs, activeCategories) ->
                activeCategories.associateWith { BigDecimal.ZERO } + activeReconcileCAs
            }
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
            { it.itemObservableMap2 }
            .map { it.mapValues { it.value.map { it.toString() }.toLiveData(errorSubject) } }
            .replay(1).refCount()
    val defaultAmount: LiveData<String> = activeReconciliationDomain2.defaultAmount
        .map { it.toString() }
        .toLiveData(errorSubject)

    // # Intents
    fun pushActiveReconcileCA(category: Category, s: String) {
        Rx.launch { reconciliationsRepo.pushActiveReconciliationCA(category to s.toMoneyBigDecimal()) }
    }

    fun saveReconciliation() {
        Rx.launch {
            Reconciliation(
                LocalDate.now(),
                activeReconciliationDomain2.defaultAmount.await(),
                reconciliationsRepo.activeReconciliationCAs.await(),
            )
                .let { reconciliationsRepo.push(it) }
                .andThen(reconciliationsRepo.clearActiveReconcileCAs())
        }
    }
}

package com.tminus1010.budgetvalue.reconciliations

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.toSingle
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDefaultAmountUC
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActiveReconciliationVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val reconciliationsRepo: IReconciliationsRepo,
    categoriesDomain: CategoriesDomain,
    private val activeReconciliationDefaultAmountUC: ActiveReconciliationDefaultAmountUC,
) : ViewModel() {
    // # Output
    val activeReconcileCAs2: Observable<Map<Category, Observable<String>>> =
        Rx.combineLatest(reconciliationsRepo.activeReconciliationCAs, categoriesDomain.userCategories)
            // These extra zeros prevent refreshes on hidden additions/removals that happen when a value is set to 0.
            .map { (activeReconcileCAs, activeCategories) ->
                activeCategories.associateWith { BigDecimal.ZERO } + activeReconcileCAs
            }
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO))
            { it.itemObservableMap2 }
            .map { it.mapValues { it.value.map { it.toString() }.divertErrors(errorSubject) } }
            .replay(1).refCount()
    val defaultAmount: Observable<String> = activeReconciliationDefaultAmountUC()
        .map { it.toString() }
        .divertErrors(errorSubject)
    // # Intents
    fun pushActiveReconcileCA(category: Category, s: String) {
        reconciliationsRepo.pushActiveReconciliationCA(category to s.toMoneyBigDecimal())
            .subscribe()
    }
    fun saveReconciliation() {
        Singles.zip(
            activeReconciliationDefaultAmountUC().toSingle(),
            reconciliationsRepo.activeReconciliationCAs.toSingle(),
        ).flatMapCompletable { (activeReconciliationDefaultAmountUC, activeReconciliationCAs) ->
            reconciliationsRepo.push(
                Reconciliation(
                    LocalDate.now(),
                    activeReconciliationDefaultAmountUC,
                    activeReconciliationCAs,
                )
            )
        }
            .andThen(reconciliationsRepo.clearActiveReconcileCAs())
            .subscribe()
    }
}

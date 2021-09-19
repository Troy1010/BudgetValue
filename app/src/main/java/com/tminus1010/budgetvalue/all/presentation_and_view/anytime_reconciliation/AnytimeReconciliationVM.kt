package com.tminus1010.budgetvalue.all.presentation_and_view.anytime_reconciliation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDefaultAmountUC
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnytimeReconciliationVM @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    categoriesDomain: CategoriesDomain,
    private val activeReconciliationDefaultAmountUC: ActiveReconciliationDefaultAmountUC,
) : ViewModel() {
    // # User Intents
    fun pushActiveReconcileCA(category: Category, s: String) {
        reconciliationsRepo.pushActiveReconciliationCA(category to s.toMoneyBigDecimal())
            .subscribe()
    }

    fun saveReconciliation() {
        Single.zip(
            activeReconciliationDefaultAmountUC().toSingle(),
            reconciliationsRepo.activeReconciliationCAs.toSingle(),
        )
        { activeReconciliationDefaultAmountUC, activeReconciliationCAs ->
            reconciliationsRepo.push(
                Reconciliation(
                    LocalDate.now(),
                    activeReconciliationDefaultAmountUC,
                    activeReconciliationCAs,
                )
            )
        }
            .flatMapCompletable { it }
            .andThen(reconciliationsRepo.clearActiveReconcileCAs())
            .subscribe()
    }

    // # Presentation Output
    // ## Events
    // ## State
    val activeReconcileCAs =
        Rx.combineLatest(
            reconciliationsRepo.activeReconciliationCAs,
            categoriesDomain.userCategories,
        )
            // These extra zeros prevent refreshes on hidden additions/removals that happen when a value is set to 0.
            .map { (activeReconcileCAs, activeCategories) ->
                activeCategories.associateWith { BigDecimal.ZERO }
                    .plus(activeReconcileCAs)
            }
            .nonLazyCache(disposables)
    val activeReconcileCAsToShow: Observable<Map<Category, Observable<String>>> =
        activeReconcileCAs
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO)) { it.itemObservableMap }
            .map { it.mapValues { it.value.map { it.toString() } } }
            .nonLazyCache(disposables)
    val defaultAmount: Observable<String> = activeReconciliationDefaultAmountUC()
        .map { it.toString() }
    val buttons =
        listOf(
            ButtonVMItem(
                title = "Save",
                onClick = ::saveReconciliation
            )
        )
}

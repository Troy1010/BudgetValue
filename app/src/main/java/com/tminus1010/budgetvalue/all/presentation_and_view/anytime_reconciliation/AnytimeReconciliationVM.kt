package com.tminus1010.budgetvalue.all.presentation_and_view.anytime_reconciliation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.all.app.interactors.SaveActiveReconciliationInteractor
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconciliations.data.ReconciliationsRepo
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AnytimeReconciliationVM @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    categoriesDomain: CategoriesDomain,
    saveActiveReconciliationInteractor: SaveActiveReconciliationInteractor
) : ViewModel() {
    // # User Intents
    fun pushActiveReconcileCA(category: Category, s: String) {
        reconciliationsRepo.pushActiveReconciliationCA(category to s.toMoneyBigDecimal())
            .subscribe()
    }

    // # Presentation Output
    // ## Events
    // ## State
    private val activeReconcileCAs = // TODO("This should be an Interactor or something.")
        Observable.combineLatest(
            reconciliationsRepo.activeReconciliationCAs,
            categoriesDomain.userCategories,
        ) { activeReconcileCAs, activeCategories ->
            // These extra zeros prevent refreshes on hidden additions/removals that happen when a value is set to 0.
            activeCategories.associateWith { BigDecimal.ZERO }
                .plus(activeReconcileCAs)
        }
            .nonLazyCache(disposables)
    val activeReconcileCAsToShow: Observable<Map<Category, Observable<String>>> =
        activeReconcileCAs
            .flatMapSourceHashMap(SourceHashMap(exitValue = BigDecimal.ZERO)) { it.itemObservableMap }
            .map { it.mapValues { it.value.map { it.toString() } } }
            .nonLazyCache(disposables)
    val defaultAmount: Observable<String> =
        activeReconcileCAs.map { it.values.sum() }
            .map(BigDecimal::toString)
    val buttons =
        listOf(
            ButtonVMItem(
                title = "Save",
                onClick = saveActiveReconciliationInteractor.saveActiveReconiliation::subscribe
            )
        )
}

package com.tminus1010.budgetvalue.reconcile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.all.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.all.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.reconcile.app.interactor.SaveActiveReconciliationInteractor
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.app.interactor.ActiveReconciliationDefaultAmountInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AnytimeReconciliationVM_OLD @Inject constructor(
    private val reconciliationsRepo: ReconciliationsRepo,
    categoriesInteractor: CategoriesInteractor,
    activeReconciliationDefaultAmountInteractor: ActiveReconciliationDefaultAmountInteractor,
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
            categoriesInteractor.userCategories,
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
        activeReconciliationDefaultAmountInteractor()
            .map(BigDecimal::toString)
    val buttons =
        listOf(
            ButtonVMItem(
                title = "Save",
                userClick = saveActiveReconciliationInteractor.saveActiveReconiliation::subscribe
            )
        )
}

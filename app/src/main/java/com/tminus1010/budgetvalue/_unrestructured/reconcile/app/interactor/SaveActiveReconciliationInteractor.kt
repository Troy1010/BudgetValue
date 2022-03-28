package com.tminus1010.budgetvalue._unrestructured.reconcile.app.interactor

import com.tminus1010.budgetvalue._unrestructured.reconcile.app.convenience_service.ActiveReconciliationDefaultAmountUC
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.domain.Reconciliation
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.framework.observable.Rx
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

class SaveActiveReconciliationInteractor @Inject constructor(
    activeReconciliationDefaultAmountUC: ActiveReconciliationDefaultAmountUC,
    activeReconciliationRepo: ActiveReconciliationRepo,
    reconciliationsRepo: ReconciliationsRepo,
) {
    val saveActiveReconciliation =
        Single.zip(
            activeReconciliationDefaultAmountUC.toSingle(),
            activeReconciliationRepo.activeReconciliationCAs.asObservable2().toSingle(),
        )
        { activeReconciliationDefaultAmountUC, activeReconciliationCAs ->
            Completable.fromAction {
                runBlocking {
                    reconciliationsRepo.push(
                        Reconciliation(
                            LocalDate.now(),
                            activeReconciliationDefaultAmountUC,
                            activeReconciliationCAs,
                        )
                    )
                }
            }.subscribeOn(Schedulers.io())
        }
            .flatMapCompletable { it }
            .andThen(Rx.completableFromSuspend { activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts()) })
}
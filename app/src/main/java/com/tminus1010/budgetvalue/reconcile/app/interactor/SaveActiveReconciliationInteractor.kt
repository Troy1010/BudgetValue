package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue._core.all.extensions.asObservable2
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue.reconcile.app.convenience_service.ActiveReconciliationDefaultAmountUC
import com.tminus1010.budgetvalue.reconcile.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.domain.Reconciliation
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Single
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
            reconciliationsRepo.push(
                Reconciliation(
                    LocalDate.now(),
                    activeReconciliationDefaultAmountUC,
                    activeReconciliationCAs,
                )
            )
        }
            .flatMapCompletable { it }
            .andThen(Rx.completableFromSuspend { activeReconciliationRepo.push(CategoryAmounts()) })
}
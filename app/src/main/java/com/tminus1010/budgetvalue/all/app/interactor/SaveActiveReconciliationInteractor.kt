package com.tminus1010.budgetvalue.all.app.interactor

import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.reconcile.app.ActiveReconciliationDefaultAmountUC
import com.tminus1010.budgetvalue.reconcile.app.Reconciliation
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Single
import java.time.LocalDate
import javax.inject.Inject

class SaveActiveReconciliationInteractor @Inject constructor(
    activeReconciliationDefaultAmountUC: ActiveReconciliationDefaultAmountUC,
    reconciliationsRepo: ReconciliationsRepo,
) {
    val saveActiveReconiliation =
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
}
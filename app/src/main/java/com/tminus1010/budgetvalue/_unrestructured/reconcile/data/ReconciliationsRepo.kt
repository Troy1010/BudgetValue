package com.tminus1010.budgetvalue._unrestructured.reconcile.data

import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.Reconciliation
import com.tminus1010.budgetvalue.data.service.MiscDAO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    fun push(reconciliation: Reconciliation): Completable =
        miscDAO.push(reconciliation).subscribeOn(Schedulers.io())

    fun delete(reconciliation: Reconciliation): Completable =
        miscDAO.delete(reconciliation).subscribeOn(Schedulers.io())

    val reconciliations: Observable<List<Reconciliation>> =
        miscDAO.fetchReconciliations().subscribeOn(Schedulers.io())
            .replay(1).refCount()
}
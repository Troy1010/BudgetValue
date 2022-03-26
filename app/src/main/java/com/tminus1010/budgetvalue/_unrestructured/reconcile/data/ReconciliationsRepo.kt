package com.tminus1010.budgetvalue._unrestructured.reconcile.data

import com.tminus1010.budgetvalue.data.service.MiscDAO
import com.tminus1010.budgetvalue._unrestructured.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.Reconciliation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter,
) {
    fun push(reconciliation: Reconciliation): Completable =
        miscDAO.push(reconciliation.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun delete(reconciliation: Reconciliation): Completable =
        miscDAO.delete(reconciliation.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    val reconciliations: Observable<List<Reconciliation>> =
        miscDAO.fetchReconciliations().subscribeOn(Schedulers.io())
            .map { it.map { Reconciliation.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()
}
package com.tminus1010.budgetvalue.reconciliations.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue._core.data.SharedPrefWrapper
import com.tminus1010.budgetvalue._core.extensions.toBigDecimalOrZero
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReconciliationsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter,
    private val sharedPrefWrapper: SharedPrefWrapper,
    categoryParser: ICategoryParser,
) {
    fun clearReconciliations(): Completable =
        miscDAO.clearReconciliations().subscribeOn(Schedulers.io())

    fun push(reconciliation: Reconciliation): Completable =
        miscDAO.add(reconciliation.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun delete(reconciliation: Reconciliation): Completable =
        miscDAO.delete(reconciliation.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    val reconciliations: Observable<List<Reconciliation>> =
        miscDAO.fetchReconciliations().subscribeOn(Schedulers.io())
            .map { it.map { Reconciliation.fromDTO(it, categoryAmountsConverter) } }
            .replay(1).refCount()

    val activeReconciliationCAs =
        sharedPrefWrapper.activeReconciliationCAs.subscribeOn(Schedulers.io())
            .map { it.associate { categoryParser.parseCategory(it.key) to it.value.toBigDecimalOrZero() } }
            .map { CategoryAmounts(it) }
            .replay(1).refCount()!!

    fun clearActiveReconcileCAs(): Completable =
        sharedPrefWrapper.clearActiveReconcileCAs().subscribeOn(Schedulers.io())

    fun pushActiveReconciliationCA(kv: Pair<Category, BigDecimal?>): Completable =
        sharedPrefWrapper.pushActiveReconciliationCA(Pair(kv.first.name, kv.second.toString())).subscribeOn(Schedulers.io())
}
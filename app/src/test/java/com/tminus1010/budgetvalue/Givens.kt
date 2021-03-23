package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue.extensions.toObservable
import com.tminus1010.budgetvalue.middleware.LocalDatePeriod
import com.tminus1010.budgetvalue.features_shared.app_init.AppInitializer
import com.tminus1010.budgetvalue.features.categories.CategoriesUCWrapper
import com.tminus1010.budgetvalue.features.categories.IUserCategoriesFetch
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features.plans.Plan
import com.tminus1010.budgetvalue.features.reconciliations.Reconciliation
import com.tminus1010.budgetvalue.features.transactions.Block
import com.tminus1010.budgetvalue.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

object Givens {
    private val givenUserCategories1: Observable<List<Category>> =
        Observable.just(AppInitializer.initCategories)
    val givenCategories = CategoriesUCWrapper(object : IUserCategoriesFetch {
        override fun fetchUserCategories(): Observable<List<Category>> = givenUserCategories1
    }).categories
    val givenPlan1 = Plan(
        localDatePeriod = Observable.just(LocalDatePeriod(LocalDate.now(), Period.ofWeeks(2))),
        defaultAmount = 60.toBigDecimal(),
        categoryAmounts = mapOf(givenCategories.value[0] to 15.toBigDecimal()))
    val givenPlan2 = Plan(
        localDatePeriod = Observable.just(LocalDatePeriod(LocalDate.now().plus(Period.ofWeeks(2)), Period.ofWeeks(2))),
        defaultAmount = 100.toBigDecimal(),
        categoryAmounts = mapOf(givenCategories.value[0] to 60.toBigDecimal(),
            givenCategories.value[1] to 31.toBigDecimal(),
            givenCategories.value[2] to 26.toBigDecimal()))
    val givenPlans = listOf(givenPlan1, givenPlan2).toObservable()
    val givenReconciliation1 = Reconciliation(
        localDate = LocalDate.now(),
        defaultAmount = 33.toBigDecimal(),
        categoryAmounts = mapOf(
            givenCategories.value[0] to 10.toBigDecimal()
        )
    )
    val givenReconciliation2 = Reconciliation(
        localDate = LocalDate.now(),
        defaultAmount = 33.toBigDecimal(),
        categoryAmounts = mapOf(
            givenCategories.value[0] to 5.toBigDecimal(),
            givenCategories.value[1] to 55.toBigDecimal()
        )
    )
    val givenReconciliations = listOf(givenReconciliation1, givenReconciliation2).toObservable()
    val givenTransactionBlock1 = Block(
        datePeriod = LocalDatePeriod(LocalDate.now().plus(Period.ofWeeks(2)), Period.ofWeeks(2)),
        amount = (-110).toBigDecimal(),
        categoryAmounts = emptyMap()
    )
    val givenTransactionBlock2 = Block(
        datePeriod = LocalDatePeriod(LocalDate.now().plus(Period.ofWeeks(2)), Period.ofWeeks(2)),
        amount = (-16).toBigDecimal(),
        categoryAmounts = mapOf(
            givenCategories.value[0] to (-9).toBigDecimal()
        )
    )
    val givenTransactionBlocks = listOf(givenTransactionBlock1, givenTransactionBlock2).toObservable()
    val givenActiveReconcileCAs: BehaviorSubject<SourceHashMap<Category, BigDecimal>> =
        SourceHashMap(
            null,
            givenCategories.value[0] to 9.toBigDecimal()
        ).toObservable().toBehaviorSubject()
    val givenAccountsTotal = BehaviorSubject.createDefault(500.toBigDecimal())
}
package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue.all_features.app.AppInitInteractor
import com.tminus1010.budgetvalue.reconcile.domain.Reconciliation
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.time.LocalDate

object Given {
    val givenCategories = Observable.just(AppInitInteractor.initCategories)
    val categories = AppInitInteractor.initCategories

    //    val givenPlan1 = Plan(
//        localDatePeriod = Observable.just(LocalDatePeriod(LocalDate.now(), Period.ofWeeks(2))),
//        amount = 60.toBigDecimal(),
//        categoryAmounts = mapOf(givenCategories.value[0] to 15.toBigDecimal()))
//    val givenPlan2 = Plan(
//        localDatePeriod = Observable.just(LocalDatePeriod(LocalDate.now().plus(Period.ofWeeks(2)), Period.ofWeeks(2))),
//        amount = 100.toBigDecimal(),
//        categoryAmounts = mapOf(givenCategories.value[0] to 60.toBigDecimal(),
//            givenCategories.value[1] to 31.toBigDecimal(),
//            givenCategories.value[2] to 26.toBigDecimal()))
//    val givenPlans = listOf(givenPlan1, givenPlan2).toObservable()
    val reconciliation1 = Reconciliation(
        localDate = LocalDate.of(2020, 1, 2),
        defaultAmount = 33.toBigDecimal(),
        categoryAmounts = mapOf(
            givenCategories.value!![0] to 10.toBigDecimal()
        )
    )
    val reconciliation2 = Reconciliation(
        localDate = LocalDate.of(2020, 1, 1),
        defaultAmount = 33.toBigDecimal(),
        categoryAmounts = mapOf(
            givenCategories.value!![0] to 5.toBigDecimal(),
            givenCategories.value!![1] to 55.toBigDecimal()
        )
    )

    //    val givenReconciliations = listOf(reconciliation1, reconciliation2).toObservable()
//    val givenTransactionBlock1 = TransactionBlock(
//        datePeriod = LocalDatePeriod(LocalDate.now().plus(Period.ofWeeks(2)), Period.ofWeeks(2)),
//        amount = (-110).toBigDecimal(),
//        categoryAmounts = emptyMap()
//    )
//    val givenTransactionBlock2 = TransactionBlock(
//        datePeriod = LocalDatePeriod(LocalDate.now().plus(Period.ofWeeks(2)), Period.ofWeeks(2)),
//        amount = (-16).toBigDecimal(),
//        categoryAmounts = mapOf(
//            givenCategories.value!![0] to (-9).toBigDecimal()
//        )
//    )
//    val givenTransactionBlocks = listOf(givenTransactionBlock1, givenTransactionBlock2).toObservable()
//    val givenActiveReconcileCAs: BehaviorSubject<SourceHashMap<Category, BigDecimal>> =
//        SourceHashMap(
//            null,
//            givenCategories.value!![0] to 9.toBigDecimal()
//        ).toObservable().toBehaviorSubject()
    val givenAccountsTotal = BehaviorSubject.createDefault(500.toBigDecimal())
}
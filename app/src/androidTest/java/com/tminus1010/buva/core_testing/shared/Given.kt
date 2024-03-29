package com.tminus1010.buva.core_testing.shared

import com.tminus1010.buva.app.InitApp
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Transaction
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import java.time.LocalDate

object Given {
    val givenCategories = Observable.just(InitApp.initCategories)
    val categories = InitApp.initCategories

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
    val transaction1 = Transaction(
        date = LocalDate.of(2020, 1, 2),
        description = "Jimmy Johns",
        amount = BigDecimal("-12.61"),
        categoryAmounts = CategoryAmounts(),
        categorizationDate = null,
        id = "eriuhtyuirethgyuidrthu"
    )
//    val reconciliation1 = Reconciliation(
//        date = LocalDate.of(2020, 1, 2),
//        defaultAmount = 33.toBigDecimal(),
//        categoryAmounts = mapOf(
//            givenCategories.value!![0] to 10.toBigDecimal()
//        )
//    )
//    val reconciliation2 = Reconciliation(
//        date = LocalDate.of(2020, 1, 1),
//        defaultAmount = 33.toBigDecimal(),
//        categoryAmounts = mapOf(
//            givenCategories.value!![0] to 5.toBigDecimal(),
//            givenCategories.value!![1] to 55.toBigDecimal()
//        )
//    )
//
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
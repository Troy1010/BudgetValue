package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.appComponent
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import com.tminus1010.budgetvalue.model_app.Plan
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.Period

//@RunWith(RobolectricTestRunner::class)
//@Config(application = App::class, manifest = "src/main/AndroidManifest.xml")
//class PlanTest {
//    val repo by lazy { appComponent.getRepo() }
//    val categoriesAppVM by lazy { appComponent.getCategoriesAppVM() }
//    val category0 by lazy { categoriesAppVM.categories.value[0] }
//    val category1 by lazy { categoriesAppVM.categories.value[1] }
//    val category2 by lazy { categoriesAppVM.categories.value[2] }
//
//    @Test
//    fun `flow 1`() {
//        // # Given
//        val givenPlan1 = Plan(Observable.just(LocalDatePeriod(LocalDate.now(), Period.ofWeeks(2))),
//            60.toBigDecimal(),
//            mapOf(category0 to 15.toBigDecimal()))
//        val givenPlan2 = Plan(Observable.just(LocalDatePeriod(LocalDate.now().plus(Period.ofWeeks(2)), Period.ofWeeks(2))),
//            100.toBigDecimal(),
//            mapOf(category0 to 60.toBigDecimal(),
//                category1 to 31.toBigDecimal(),
//                category2 to 26.toBigDecimal()))
//        // # Stimulate & Verify
//        repo.fetchPlans().map { it.size }.test()
//            .apply {
//                awaitCount(1)
//                assertValueAt(0, 0)
//                repo.pushPlan(givenPlan1)
//                awaitCount(2)
//                assertValueAt(1, 1)
//                repo.pushPlan(givenPlan2)
//                awaitCount(3)
//                assertValueAt(2, 2)
//                repo.clearPlans().subscribeOn(Schedulers.io()).subscribe()
//                awaitCount(4)
//                assertValueAt(3, 0)
//            }
//    }
//}
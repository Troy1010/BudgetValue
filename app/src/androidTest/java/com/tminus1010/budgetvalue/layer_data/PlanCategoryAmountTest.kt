package com.tminus1010.budgetvalue.layer_data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.AppMock
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.model_data.PlanCategoryAmount
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class PlanCategoryAmountTest {
    val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AppMock }
    val repo by lazy { app.appComponent.getRepo() }
    val x by lazy {
        app.appComponent.getCategoriesAppVM()
            .userAddedCategories
            .addAll(listOf(
                Category("SomeCategoryA", Category.Type.Always),
                Category("SomeCategoryB", Category.Type.Always),
                Category("SomeCategoryC", Category.Type.Always)
            ))
    }

    @Before
    fun before() {
        repo.clearPlanCategoryAmounts().blockingAwait()
        x
    }

    @Test
    fun addAndGetPlanCategoryAmountsTest() {
        // # Given
        val planCategoryAmounts = PlanCategoryAmount("SomeCategory", BigDecimal.TEN)
        // # Stimulate
        repo.add(planCategoryAmounts).blockingAwait()
        // # Verify
        assertEquals(1, repo.getPlanCategoryAmountsReceived().blockingFirst().size)
        assertEquals(planCategoryAmounts, repo.getPlanCategoryAmountsReceived().blockingFirst().toList().first())
    }

    @Test
    fun clearPlanCategoryAmountTest() {
        // # Given
        repo.add(PlanCategoryAmount("SomeCategoryA", BigDecimal.TEN)).blockingAwait()
        repo.add(PlanCategoryAmount("SomeCategoryB", BigDecimal.TEN)).blockingAwait()
        repo.add(PlanCategoryAmount("SomeCategoryC", BigDecimal.TEN)).blockingAwait()
        assertEquals(3, repo.getPlanCategoryAmountsReceived().blockingFirst().size)
        // # Stimulate
        repo.clearPlanCategoryAmounts().blockingAwait()
        // # Verify
        assertEquals(0, repo.getPlanCategoryAmountsReceived().blockingFirst().size)
    }

    // it.observable no longer supported
//    @Test
//    fun integrationTest1() {
//        // # Given
//        repo.add(PlanCategoryAmount("SomeCategoryA", BigDecimal.TEN)).blockingAwait()
//        repo.add(PlanCategoryAmount("SomeCategoryB", BigDecimal.TEN)).blockingAwait()
//        repo.add(PlanCategoryAmount("SomeCategoryC", BigDecimal.TEN)).blockingAwait()
//        assertEquals(3, repo.getPlanCategoryAmountsReceived().blockingFirst().size)
//        // # Stimulate
//        repo.planCategoryAmounts
//            .subscribeOn(Schedulers.trampoline())
//            .take(1)
//            .flatMap { it.observable }
//            .doOnNext { it.values.zip(listOf(64, 22, 39)).forEach { (bs, v) -> bs.onNext(BigDecimal(v)) } }
//            .subscribe()
//        // # Verify
//        Thread.sleep(1000) // TODO("add RxThreadsRule")
//        val results = repo.getPlanCategoryAmountsReceived().blockingFirst()
//        assertEquals(BigDecimal(64), results[0].amount)
//        assertEquals(BigDecimal(22), results[1].amount)
//        assertEquals(BigDecimal(39), results[2].amount)
//    }
}
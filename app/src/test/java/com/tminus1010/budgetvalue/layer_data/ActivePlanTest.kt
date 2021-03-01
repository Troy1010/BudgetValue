package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.appComponent
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = App::class, manifest = "src/main/AndroidManifest.xml")
class ActivePlanTest {
    val repo by lazy { appComponent.getRepo() }
    val categoriesAppVM by lazy { appComponent.getCategoriesAppVM() }
    val category0 by lazy { categoriesAppVM.categories.value[0] }
    val category1 by lazy { categoriesAppVM.categories.value[1] }
    val category2 by lazy { categoriesAppVM.categories.value[2] }

    @Test
    fun `flow 1`() {
        // # Given
        val givenPlanCA0 = category0 to 15.toBigDecimal()
        val givenPlanCA1 = category1 to 30.toBigDecimal()
        val givenPlanCA2 = category2 to 45.toBigDecimal()
        // # Stimulate
        val stopObservable = PublishSubject.create<Unit>()
        val testObserver = repo.activePlanCAs.map { it.size }.takeUntil(stopObservable).test()
        repo.pushActivePlanCA(givenPlanCA0)
        repo.pushActivePlanCA(givenPlanCA1)
        repo.pushActivePlanCA(givenPlanCA2)
        repo.clearActivePlanCAs()
        stopObservable.onNext(Unit)
        // # Verify
        testObserver.assertResult(0, 1, 2, 3, 0)
    }
}
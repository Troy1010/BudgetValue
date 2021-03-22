package com.tminus1010.budgetvalue.layer_data

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
//        // # Stimulate
//        val testObserver = repo.plans.map { it.size }.take(2).test()
//        repo.pushPlan(givenPlan1)
//        repo.pushPlan(givenPlan2)
//        // # Verify
//        testObserver.awaitCount(2)
//        testObserver.assertValues(0, 2)
//    }
//}
package com.tminus1010.budgetvalue.replay_or_future

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.schedulers.Schedulers

class CreateFutureVMTest {
//    @Test
//    fun test() {
//        // # Given
//        val givenSelectedCategories = listOf(
//            Given.categories[0],
//            Given.categories[1],
//            Given.categories[2],
//            Given.categories[3],
//        )
//        val givenCategorySelectionVM = mockk<CategorySelectionVM> {
//            every { selectedCategories } returns Observable.just(givenSelectedCategories)
//        }
//        // # When
//        val createFutureVM =
//            CreateFutureVM(
//                object : ICategoryParser {
//                    override fun parseCategory(categoryName: String): Category {
//                        return Given.categories.find { it.name == categoryName }!!
//                    }
//                },
//                mockk()
//            )
//        createFutureVM.setup(givenCategorySelectionVM)
//        createFutureVM.userSetFillCategory(Given.categories[1].name)
//        createFutureVM.userSetCategoryIsPercentage(Given.categories[1], true)
//        createFutureVM.userInputCA(Given.categories[1], BigDecimal("10"))
//        createFutureVM.userInputCA(Given.categories[3], BigDecimal("-1"))
//        createFutureVM.userInputCA(Given.categories[3], BigDecimal("1"))
//        createFutureVM.userSetFillCategory(Given.categories[2].name)
//        createFutureVM.userSetTotalGuess("100")
//        // # Then
//        assertEquals(
//            mapOf(
//                Given.categories[0] to AmountFormula.Value(BigDecimal("0")),
//                Given.categories[1] to AmountFormula.Percentage(BigDecimal("10")),
//                Given.categories[2] to AmountFormula.Value(BigDecimal("0")),
//                Given.categories[3] to AmountFormula.Value(BigDecimal("1")),
//            ),
//            createFutureVM._categoryAmountFormulas.value,
//        )
//        assertEquals(
//            Given.categories[2],
//            createFutureVM._fillCategory.value,
//        )
//        assertEquals(
//            BigDecimal("100.00"),
//            createFutureVM._totalGuess.value,
//        )
//    }

    init {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }
}
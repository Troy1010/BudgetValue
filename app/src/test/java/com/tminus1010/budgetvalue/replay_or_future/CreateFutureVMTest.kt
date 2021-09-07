package com.tminus1010.budgetvalue.replay_or_future

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CreateFutureVMTest {
    @Test
    fun test() {
        // # Given
        val givenSelectedCategories = listOf(
            Given.categories[0],
            Given.categories[1],
            Given.categories[2],
            Given.categories[3],
        )
        val givenCategorySelectionVM = mockk<CategorySelectionVM> {
            every { selectedCategories } returns Observable.just(givenSelectedCategories)
        }
        // # When
        val createFutureVM =
            CreateFutureVM(
                object : ICategoryParser {
                    override fun parseCategory(categoryName: String): Category {
                        return Given.categories.find { it.name == categoryName }!!
                    }
                },
                mockk()
            )
        createFutureVM.setup(givenCategorySelectionVM)
        createFutureVM.userSetFillCategory(Given.categories[1].name)
        createFutureVM.userSetCategoryIsPercentage(Given.categories[1], true)
        createFutureVM.userInputCA(Given.categories[1], BigDecimal("10"))
        createFutureVM.userInputCA(Given.categories[3], BigDecimal("-1"))
        createFutureVM.userInputCA(Given.categories[3], BigDecimal("1"))
        createFutureVM.userSetFillCategory(Given.categories[2].name)
        createFutureVM.userSetTotalGuess("100")
        // # Then
        assertEquals(
            mapOf(
                Given.categories[0] to AmountFormula.Value(BigDecimal("0")),
                Given.categories[1] to AmountFormula.Percentage(BigDecimal("10")),
                Given.categories[2] to AmountFormula.Value(BigDecimal("0")),
                Given.categories[3] to AmountFormula.Value(BigDecimal("1")),
            ),
            createFutureVM.categoryAmountFormulas.value,
        )
        assertEquals(
            Given.categories[2],
            createFutureVM.fillCategory.value,
        )
        assertEquals(
            BigDecimal("100.00"),
            createFutureVM._totalGuess.value,
        )
    }

    init {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }
}
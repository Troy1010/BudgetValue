package com.tminus1010.budgetvalue.plans.data

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesAdapters
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
class PlanAdapterTest {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var datePeriodService: DatePeriodService
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Before
    fun before() {
        hiltAndroidRule.inject()
        moshiWithCategoriesProvider =
            MoshiWithCategoriesProvider(
                MoshiWithCategoriesAdapters(
                    CategoriesInteractor(
                        mockk {
                            every { userCategories } returns
                                    flowOf(listOf(), Given.categories)
                        }
                    )
                )
            )
    }

    @Test
    fun toFromJson() {
        // # Given
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9")),
            )
        // # When
        val result =
            moshiWithCategoriesProvider.moshi.toJson(givenPlan)
                .let { moshiWithCategoriesProvider.moshi.fromJson<Plan>(it) }
        // # Then
        assertEquals(givenPlan, result)
    }
}
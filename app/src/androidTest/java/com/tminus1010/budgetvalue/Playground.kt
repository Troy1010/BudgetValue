package com.tminus1010.budgetvalue

import com.squareup.moshi.Types
import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue._core.app.DatePeriodService
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesAdapters
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.budgetvalue.plans.domain.Plan2
import com.tminus1010.tmcommonkotlin.core.logx
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
class Playground {
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
                                    flow { emit(listOf()); emit(Given.categories) }
                        }
                    )
                )
            )
    }

    @Test
    fun test() {
        // # Given
        val givenStr =
            """{"localDatePeriod":{"startDate":"10/20/2021","endDate":"11/02/2021"},"amount":"11","categoryAmounts":{"Food":"9"}}"""
        // # When
        val result = moshiWithCategoriesProvider.moshi.fromJson<Plan>(givenStr)
        // # Then
        assertEquals(
            Plan(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                mapOf(Given.categories[0] to BigDecimal("9"))
            ),
            result,
        )
    }

    @Test
    fun testA() {
        val x = datePeriodService.getDatePeriod(LocalDate.now())
        assertEquals(
            Plan2(
                x,
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            ),
            Plan2(
                x,
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            ),
        )
    }

    @Test
    fun test2() {
        // # Given
        val givenPlan =
            Plan2(
                datePeriodService.getDatePeriod(LocalDate.now()),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        // # When
        val result =
            moshiWithCategoriesProvider.moshi.toJson(givenPlan)
                .logx("json")
                .let { moshiWithCategoriesProvider.moshi.fromJson<Plan2>(it) }
        // # Then
        assertEquals(
            givenPlan,
            result,
        )
    }

//    @Test
//    fun test2B() {
//        // # Given
//        val givenPlan =
//            Plan3(
//                datePeriodService.getDatePeriod(LocalDate.now()),
//                BigDecimal("11"),
//                mapOf(Given.categories[0] to BigDecimal("9"))
//            )
//        // # When
//        val result =
//            moshiWithCategoriesProvider.moshi.toJson(givenPlan)
//                .logx("json")
//                .let { moshiWithCategoriesProvider.moshi.fromJson<Plan3>(it) }
//        // # Then
//        assertEquals(
//            givenPlan,
//            result,
//        )
//    }

    @Test
    fun test2C() {
        // # Given
        val givenCategoryAmounts =
            mutableMapOf(Given.categories[0] to BigDecimal("9"))
        val adapter =
            moshiWithCategoriesProvider.moshi
                .adapter<MutableMap<Category, BigDecimal>>(
                    Types.newParameterizedType(MutableMap::class.java, Category::class.java, BigDecimal::class.java)
                )
        // # When
        val result =
            adapter.toJson(givenCategoryAmounts)
                .logx("json")
//                .let { moshiWithCategoriesProvider.moshi.fromJson<Map<Category, BigDecimal>>(it) }
                .let {
                    adapter.fromJson(it)!!
                }
        // # Then
        assertEquals(
            givenCategoryAmounts.entries.first().key.name,
            result.entries.first().key.name,
        )
//        assertEquals(
//            givenCategoryAmounts.entries.first().value,
//            result.entries.first().value,
//        )
    }

    @Test
    fun test3() {
        // # Given
        val givenCategoryAmounts =
            mapOf(Given.categories[0] to "9")
        // # When
        val result =
            moshiWithCategoriesProvider.moshi.toJson(givenCategoryAmounts)
                .logx("json")
                .let { moshiWithCategoriesProvider.moshi.fromJson<Map<Category, String>>(it) }
        // # Then
        assertEquals(
            givenCategoryAmounts.mapValues { it.value.toString() }.toMap(),
            result.mapValues { it.value.toString() }.toMap(),
        )
    }
}
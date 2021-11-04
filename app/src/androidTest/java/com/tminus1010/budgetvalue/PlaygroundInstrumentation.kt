package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue._core.domain.DatePeriodService
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesAdapters
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import javax.inject.Inject

@Ignore
@HiltAndroidTest
class PlaygroundInstrumentation {
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
}
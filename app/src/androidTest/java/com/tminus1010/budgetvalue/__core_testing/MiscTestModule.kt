package com.tminus1010.budgetvalue.__core_testing

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesAdapters
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MiscTestModule {
    @Provides
    @Singleton
    fun provideMoshiWithCategoriesProvider(): MoshiWithCategoriesProvider =
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
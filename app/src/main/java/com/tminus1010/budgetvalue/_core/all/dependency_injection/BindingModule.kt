package com.tminus1010.budgetvalue._core.all.dependency_injection

import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
    @Binds
    abstract fun providesCategoryParser(categoriesInteractor: CategoriesInteractor): ICategoryParser
}
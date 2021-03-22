package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.FlavorIntersection
import com.tminus1010.budgetvalue.IFlavorIntersection
import com.tminus1010.budgetvalue.modules.categories.IUserCategoriesFetch
import com.tminus1010.budgetvalue.modules.categories.UserCategoriesUseCases
import com.tminus1010.budgetvalue.modules.categories.UserCategoriesUseCasesImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BindingModule {
    @Binds
    abstract fun getUserCategoriesUseCases(userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl): UserCategoriesUseCases

    @Binds
    abstract fun getUserCategoriesFetchUseCase(userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl): IUserCategoriesFetch

    @Binds
    abstract fun provideFlavorIntersection(flavorIntersection: FlavorIntersection): IFlavorIntersection
}
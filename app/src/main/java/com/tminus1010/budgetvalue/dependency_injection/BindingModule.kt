package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.layer_domain.use_cases.IUserCategoriesFetch
import com.tminus1010.budgetvalue.layer_domain.use_cases.UserCategoriesUseCases
import com.tminus1010.budgetvalue.layer_domain.use_cases.UserCategoriesUseCasesImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BindingModule {
    @Binds
    abstract fun getUserCategoriesUseCases(userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl): UserCategoriesUseCases

    @Binds
    abstract fun getUserCategoriesFetchUseCase(userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl): IUserCategoriesFetch
}
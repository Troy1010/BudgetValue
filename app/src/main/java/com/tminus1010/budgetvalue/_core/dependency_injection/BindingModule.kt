package com.tminus1010.budgetvalue._core.dependency_injection

import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.SettingsUseCases
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.SettingsUseCasesImpl
import com.tminus1010.budgetvalue.categories.IUserCategoriesFetch
import com.tminus1010.budgetvalue.categories.UserCategoriesUseCases
import com.tminus1010.budgetvalue.categories.UserCategoriesUseCasesImpl
import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue.plans.PlanUseCasesImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BindingModule {
    @Binds
    abstract fun getUserCategoriesUseCases(userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl): UserCategoriesUseCases

    @Binds
    abstract fun getUserCategoriesFetchUseCase(userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl): IUserCategoriesFetch

    @Binds
    abstract fun provideSettingsUseCases(settingsUseCasesImpl: SettingsUseCasesImpl): SettingsUseCases

    @Binds
    abstract fun providePlanUseCases(planUseCasesImpl: PlanUseCasesImpl): PlanUseCases
}
package com.tminus1010.budgetvalue.dependency_injection

import com.tminus1010.budgetvalue.FlavorIntersection
import com.tminus1010.budgetvalue.IFlavorIntersection
import com.tminus1010.budgetvalue.features.categories.IUserCategoriesFetch
import com.tminus1010.budgetvalue.features.categories.UserCategoriesUseCases
import com.tminus1010.budgetvalue.features.categories.UserCategoriesUseCasesImpl
import com.tminus1010.budgetvalue.features.plans.PlanUseCases
import com.tminus1010.budgetvalue.features.plans.PlanUseCasesImpl
import com.tminus1010.budgetvalue.features_shared.SettingsUseCases
import com.tminus1010.budgetvalue.features_shared.SettingsUseCasesImpl
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

    @Binds
    abstract fun provideSettingsUseCases(settingsUseCasesImpl: SettingsUseCasesImpl): SettingsUseCases

    @Binds
    abstract fun providePlanUseCases(planUseCasesImpl: PlanUseCasesImpl): PlanUseCases
}
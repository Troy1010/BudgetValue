package com.tminus1010.budgetvalue._core.dependency_injection

import com.tminus1010.budgetvalue._core.ui.GetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.flavor_contracts.development_production.IGetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.flavor_contracts.development_production.ILaunchImportUC
import com.tminus1010.budgetvalue._core.ui.LaunchImportUC
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorContractModule {
    @Binds
    abstract fun getUserCategoriesUseCases(launchImportUC: LaunchImportUC): ILaunchImportUC

    @Binds
    abstract fun getUserCategoriesFetchUseCase(getExtraMenuItemPartialsUC: GetExtraMenuItemPartialsUC): IGetExtraMenuItemPartialsUC
}
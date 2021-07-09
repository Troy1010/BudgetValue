package com.tminus1010.budgetvalue._core.dependency_injection

import com.tminus1010.budgetvalue._core.data.MainRepo
import com.tminus1010.budgetvalue._shared.app_init.data.IAppInitRepo
import com.tminus1010.budgetvalue._shared.date_period_getter.data.ISettingsRepo
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
    @Binds
    abstract fun providesCategoryParser(categoriesDomain: CategoriesDomain): ICategoryParser

    @Binds
    abstract fun reconciliationRepo(mainRepo: MainRepo): IReconciliationsRepo

    @Binds
    abstract fun settingsRepo(mainRepo: MainRepo): ISettingsRepo

    @Binds
    abstract fun appInitRepo(mainRepo: MainRepo): IAppInitRepo

    @Binds
    abstract fun transactionsRepo(mainRepo: MainRepo): ITransactionsRepo
}
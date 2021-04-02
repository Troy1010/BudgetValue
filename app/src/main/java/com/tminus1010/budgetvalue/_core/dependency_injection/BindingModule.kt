package com.tminus1010.budgetvalue._core.dependency_injection

import com.tminus1010.budgetvalue._core.data.RepoFacade
import com.tminus1010.budgetvalue._shared.app_init.data.IAppInitRepo
import com.tminus1010.budgetvalue._shared.date_period_getter.data.ISettingsRepo
import com.tminus1010.budgetvalue.accounts.data.IAccountsRepo
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.data.ICategoriesRepo
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
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
    abstract fun categoriesRepo(categoriesRepo: CategoriesRepo): ICategoriesRepo

    @Binds
    abstract fun accountsRepo(repoFacade: RepoFacade): IAccountsRepo

    @Binds
    abstract fun reconciliationRepo(repoFacade: RepoFacade): IReconciliationsRepo

    @Binds
    abstract fun plansRepo(repoFacade: RepoFacade): IPlansRepo

    @Binds
    abstract fun settingsRepo(repoFacade: RepoFacade): ISettingsRepo

    @Binds
    abstract fun appInitRepo(repoFacade: RepoFacade): IAppInitRepo

    @Binds
    abstract fun transactionsRepo(repoFacade: RepoFacade): ITransactionsRepo
}
package com.tminus1010.budgetvalue._core.data

import com.tminus1010.budgetvalue._shared.app_init.data.IAppInitRepo
import com.tminus1010.budgetvalue._shared.date_period_getter.data.ISettingsRepo
import com.tminus1010.budgetvalue.accounts.data.IAccountsRepo
import com.tminus1010.budgetvalue.categories.data.ICategoriesRepo
import com.tminus1010.budgetvalue.plans.data.IPlansRepo
import com.tminus1010.budgetvalue.reconciliations.data.IReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo

interface IMainRepo :
    IAccountsRepo,
    IAppInitRepo,
    ICategoriesRepo,
    ISettingsRepo,
    IReconciliationsRepo,
    ITransactionsRepo,
    IPlansRepo

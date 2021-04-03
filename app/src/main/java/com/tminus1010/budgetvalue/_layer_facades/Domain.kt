package com.tminus1010.budgetvalue._layer_facades

import com.tminus1010.budgetvalue.accounts.AccountUseCases
import com.tminus1010.budgetvalue.accounts.AccountUseCasesImpl
import com.tminus1010.budgetvalue.categories.*
import com.tminus1010.budgetvalue.plans.PlanUseCases
import com.tminus1010.budgetvalue.plans.PlanUseCasesImpl
import com.tminus1010.budgetvalue.reconciliations.ReconciliationUseCases
import com.tminus1010.budgetvalue.reconciliations.ReconciliationUseCasesImpl
import com.tminus1010.budgetvalue.transactions.ITransactionParser
import com.tminus1010.budgetvalue.transactions.TransactionParser
import com.tminus1010.budgetvalue.transactions.TransactionUseCases
import com.tminus1010.budgetvalue.transactions.TransactionUseCasesImpl
import com.tminus1010.budgetvalue._core.shared_features.app_init.AppInitBoolUseCases
import com.tminus1010.budgetvalue._core.shared_features.app_init.AppInitBoolUseCasesImpl
import com.tminus1010.budgetvalue._core.shared_features.app_init.AppInitializer
import com.tminus1010.budgetvalue._core.shared_features.app_init.IAppInitializer
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.IDatePeriodGetter
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.SettingsUseCases
import com.tminus1010.budgetvalue._core.shared_features.date_period_getter.SettingsUseCasesImpl
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Domain is the facade to the domain layer.
 * If you ever change the business logic, all other layers will not require updates.
 */
@Singleton
class Domain @Inject constructor(
    private val appInitializer: AppInitializer,
    private val datePeriodGetter: DatePeriodGetter,
    private val transactionParser: TransactionParser,
    private val accountUseCasesImpl: AccountUseCasesImpl,
    private val appInitBoolUseCasesImpl: AppInitBoolUseCasesImpl,
    private val planUseCasesImpl: PlanUseCasesImpl,
    private val reconciliationUseCasesImpl: ReconciliationUseCasesImpl,
    private val settingsUseCasesImpl: SettingsUseCasesImpl,
    private val transactionUseCasesImpl: TransactionUseCasesImpl,
    private val userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl
) : IAppInitializer by appInitializer,
    IDatePeriodGetter by datePeriodGetter,
    ITransactionParser by transactionParser,
    AccountUseCases by accountUseCasesImpl,
    AppInitBoolUseCases by appInitBoolUseCasesImpl,
    PlanUseCases by planUseCasesImpl,
    ReconciliationUseCases by reconciliationUseCasesImpl,
    SettingsUseCases by settingsUseCasesImpl,
    TransactionUseCases by transactionUseCasesImpl,
    UserCategoriesUseCases by userCategoriesUseCasesImpl
package com.tminus1010.budgetvalue.features_shared

import com.tminus1010.budgetvalue.features.accounts.AccountUseCases
import com.tminus1010.budgetvalue.features.accounts.AccountUseCasesImpl
import com.tminus1010.budgetvalue.features.categories.*
import com.tminus1010.budgetvalue.features.plans.PlanUseCases
import com.tminus1010.budgetvalue.features.plans.PlanUseCasesImpl
import com.tminus1010.budgetvalue.features.reconciliations.ReconciliationUseCases
import com.tminus1010.budgetvalue.features.reconciliations.ReconciliationUseCasesImpl
import com.tminus1010.budgetvalue.features.transactions.ITransactionParser
import com.tminus1010.budgetvalue.features.transactions.TransactionParser
import com.tminus1010.budgetvalue.features.transactions.TransactionUseCases
import com.tminus1010.budgetvalue.features.transactions.TransactionUseCasesImpl
import com.tminus1010.budgetvalue.features_shared.app_init.AppInitBoolUseCases
import com.tminus1010.budgetvalue.features_shared.app_init.AppInitBoolUseCasesImpl
import com.tminus1010.budgetvalue.features_shared.app_init.AppInitializer
import com.tminus1010.budgetvalue.features_shared.app_init.IAppInitializer
import io.reactivex.rxjava3.core.Completable
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
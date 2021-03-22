package com.tminus1010.budgetvalue.features_shared

import com.tminus1010.budgetvalue.features.accounts.AccountUseCases
import com.tminus1010.budgetvalue.features.accounts.AccountUseCasesImpl
import com.tminus1010.budgetvalue.features.categories.*
import com.tminus1010.budgetvalue.features.plans.ExpectedIncomeUseCases
import com.tminus1010.budgetvalue.features.plans.ExpectedIncomeUseCasesImpl
import com.tminus1010.budgetvalue.features.plans.PlanUseCases
import com.tminus1010.budgetvalue.features.plans.PlanUseCasesImpl
import com.tminus1010.budgetvalue.features.reconciliations.ReconciliationUseCases
import com.tminus1010.budgetvalue.features.reconciliations.ReconciliationUseCasesImpl
import com.tminus1010.budgetvalue.features.transactions.ITransactionParser
import com.tminus1010.budgetvalue.features.transactions.TransactionParser
import com.tminus1010.budgetvalue.features.transactions.TransactionUseCases
import com.tminus1010.budgetvalue.features.transactions.TransactionUseCasesImpl
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
    private val categoriesUCWrapper: CategoriesUCWrapper,
    private val transactionParser: TransactionParser,
    private val accountUseCasesImpl: AccountUseCasesImpl,
    private val appInitBoolUseCasesImpl: AppInitBoolUseCasesImpl,
    private val expectedIncomeUseCasesImpl: ExpectedIncomeUseCasesImpl,
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
    ExpectedIncomeUseCases by expectedIncomeUseCasesImpl,
    PlanUseCases by planUseCasesImpl,
    ReconciliationUseCases by reconciliationUseCasesImpl,
    SettingsUseCases by settingsUseCasesImpl,
    TransactionUseCases by transactionUseCasesImpl,
    UserCategoriesUseCases by userCategoriesUseCasesImpl,
    ICategories by categoriesUCWrapper {

    fun deleteFromActive(category: Category): Completable =
        Completable.merge(listOf(
            pushActivePlanCA(Pair(category, null)),
            pushActiveReconciliationCA(Pair(category, null)),
            delete(category),
        ))

    fun deleteFromEverywhere(category: Category): Completable =
        Completable.merge(listOf(
            deleteFromActive(category),
            transactions
                .take(1)
                .flatMapCompletable { Completable.merge(it.map { pushTransactionCA(it, category, null) }) },
            reconciliations
                .take(1)
                .flatMapCompletable { Completable.merge(it.map { pushReconciliationCA(it, category, null) }) },
            plans
                .take(1)
                .flatMapCompletable { Completable.merge(it.map { pushPlanCA(it, category, null) }) },
        ))
}
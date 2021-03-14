package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.layer_domain.use_cases.*
import com.tminus1010.budgetvalue.model_domain.Category
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
    private val userCategories: UserCategories,
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
    IUserCategories by userCategories {

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
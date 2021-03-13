package com.tminus1010.budgetvalue.layer_domain

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
    private val typeConverter: TypeConverter,
    private val repoWrapper: RepoWrapper,
    private val activeCategoriesDAOWrapper: ActiveCategoriesDAOWrapper,
    private val transactionParser: TransactionParser,
) : IAppInitializer by appInitializer,
    IDatePeriodGetter by datePeriodGetter,
    ITypeConverter by typeConverter,
    IRepoWrapper by repoWrapper,
    IActiveCategoriesDAOWrapper by activeCategoriesDAOWrapper,
    ITransactionParser by transactionParser {

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